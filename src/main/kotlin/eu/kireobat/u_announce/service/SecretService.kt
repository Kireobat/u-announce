package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.api.dto.CreateSecretDto
import eu.kireobat.u_announce.api.dto.SecretDto
import eu.kireobat.u_announce.persistence.entity.SecretEntity
import eu.kireobat.u_announce.persistence.repo.SecretRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.crypto.spec.SecretKeySpec

@Service
class SecretService(
    private val organizationService: OrganizationService,
    private val encryptionService: EncryptionService,
    private val platformService: PlatformService,
    private val secretRepo: SecretRepo,
    private val credentialService: CredentialService
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
        }
    }

    fun getSecret(secretId: Long, userId: String): SecretDto {
        val secretEntity = secretRepo.findById(secretId).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Could not find secret with id ($secretId)"
        )
        }

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
}