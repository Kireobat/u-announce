package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.api.dto.CreateIntegrationStatusDto
import eu.kireobat.u_announce.api.dto.PatchIntegrationStatusDto
import eu.kireobat.u_announce.api.dto.CreateSecretDto
import eu.kireobat.u_announce.api.dto.SecretDto
import eu.kireobat.u_announce.persistence.entity.SecretEntity
import eu.kireobat.u_announce.persistence.entity.IntegrationStatusEntity
import eu.kireobat.u_announce.persistence.repo.SecretRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.crypto.spec.SecretKeySpec
import java.util.Optional

@Service
class SecretService(
    private val organizationService: OrganizationService,
    private val encryptionService: EncryptionService,
    private val platformService: PlatformService,
    private val secretRepo: SecretRepo,
    private val credentialService: CredentialService,
    private val integrationStatusService: IntegrationStatusService
) {

    fun createSecret(createSecretDto: CreateSecretDto, userId: String) {
        val platformEntity = platformService.getPlatform(createSecretDto.platformId)
        val organizationEntity = organizationService.getOrganization(createSecretDto.orgId,userId)

        val cleanJsonCredentials = credentialService.validateAndSerialize(
            platformEntity,
            createSecretDto.credentials
        )

        val plaintextOrgKey = encryptionService.decrypt(organizationEntity.organizationKey)
        val orgKeySpec = SecretKeySpec(plaintextOrgKey, "AES")

        try {

            val encryptedCredentials = encryptionService.encrypt(cleanJsonCredentials.toByteArray(Charsets.UTF_8), orgKeySpec)

            secretRepo.saveAndFlush(SecretEntity().apply {
                organizationId = organizationEntity.id
                platformId = platformEntity.id
                this.encryptedCredential = encryptedCredentials
                keycloakCreatedByUserId = userId
            })
        } finally {
            plaintextOrgKey.fill(0)

            // Check if an integration status exists, if not, create one

            val existingIntegrationStatus = integrationStatusService.getIntegrationStatus(organizationEntity.id, platformEntity.id, userId)
            
            if (existingIntegrationStatus.isPresent) {
                integrationStatusService.patchIntegrationStatus(
                    PatchIntegrationStatusDto(
                        id = existingIntegrationStatus.get().id,
                        active = true
                    ),
                    userId
            )
            } else {
                integrationStatusService.setupIntegrationStatus(CreateIntegrationStatusDto(organizationEntity.id, platformEntity.id, true ), userId)
            }
        }
    }

    fun getSecretByPlatformAndOrg(platformId: Long, orgId: Long, userId: String): SecretDto {
        val organizationEntity = organizationService.getOrganization(orgId, userId)
        val platformEntity = platformService.getPlatform(platformId)

        val secretEntity = secretRepo.findByOrganizationIdAndPlatformId(organizationEntity.id,platformEntity.id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Organization '${organizationEntity.displayName}' does not have a secret for platform '${platformEntity.displayName}'"
        )
        }

        return getSecret(secretEntity, userId)
    }

    fun getSecretById(secretId: Long, userId: String): SecretDto {
        val secretEntity = secretRepo.findById(secretId).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Could not find secret with id ($secretId)"
        )
        }

        return getSecret(secretEntity, userId)
    }

    private fun getSecret(secretEntity: SecretEntity, userId: String): SecretDto {
        val organizationEntity = organizationService.getOrganization(secretEntity.organizationId, userId)

        val platformEntity = platformService.getPlatform(secretEntity.platformId)

        val plaintextOrgKey = encryptionService.decrypt(organizationEntity.organizationKey)

        val orgKeySpec = SecretKeySpec(plaintextOrgKey, "AES")

        try {
            val decryptedCredentialsBytes = encryptionService.decrypt(secretEntity.encryptedCredential, orgKeySpec)

            // Convert the resulting byte arrays to strings
            val credentialsString = String(decryptedCredentialsBytes, Charsets.UTF_8)

            val credentials = credentialService.validateAndDeserialize(platformEntity, credentialsString)

            return SecretDto(secretEntity.id, organizationEntity.id, secretEntity.platformId, credentials)
        } finally {
            plaintextOrgKey.fill(0)
        }
    }

    fun deleteSecretById(secretId: Long, userId: String) {
        
        val secretEntity = getSecretById(secretId, userId)

        val organizationEntity = organizationService.getOrganization(secretEntity.orgId, userId)

        val integrationStatusEntity = integrationStatusService.getIntegrationStatus(organizationEntity.id, secretEntity.platformId, userId).orElseThrow { 
            ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find integrationStatus for orgId (${organizationEntity.id}) and platformId (${secretEntity.platformId})")
        }

        integrationStatusService.patchIntegrationStatus(
            PatchIntegrationStatusDto(
                id = integrationStatusEntity.id,
                active = false
            ),
            userId
        )

        secretRepo.deleteById(secretId)

    }

    fun deleteSecretByOrgId(orgId: Long, userId: String) {
        val organizationEntity = organizationService.getOrganization(orgId, userId)

        secretRepo.deleteAllByOrganizationId(organizationEntity.id)
    }
}