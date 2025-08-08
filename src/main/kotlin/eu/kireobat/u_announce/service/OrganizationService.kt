package eu.kireobat.u_announce.service;

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.getSlug
import eu.kireobat.u_announce.api.dto.validate
import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.persistence.repo.OrganizationRepo
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.GroupRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.SecureRandom

@Service
class OrganizationService(
    private val organizationRepo: OrganizationRepo,
    private val encryptionService: EncryptionService,
    private val keycloakService: KeycloakService,
) {

    fun createOrganization(createOrganizationDto: CreateOrganizationDto, userId: String) {

        createOrganizationDto.validate()

        require(organizationRepo.findByDisplayName(createOrganizationDto.displayName).isEmpty) { "Organization name must be unique" }

        val generatedSlug = createOrganizationDto.getSlug()

        val optKeycloakGroupId = keycloakService.createOrganization(generatedSlug, userId)

        require(optKeycloakGroupId.isPresent) { "Error creating organization." }

        // generate organization key
        val secureRandom = SecureRandom()
        val newOrganizationKey = ByteArray(32)

        try {
            secureRandom.nextBytes(newOrganizationKey)

            val encryptedOrganizationKey = encryptionService.encrypt(newOrganizationKey)

            organizationRepo.saveAndFlush(OrganizationEntity().apply {
                slug = generatedSlug
                displayName = createOrganizationDto.displayName
                organizationKey = encryptedOrganizationKey
                keycloakGroupId = optKeycloakGroupId.get()
                keycloakCreatedByUserId = userId
            })
        } finally {
            // Overwrite the plaintext key with zeros
            newOrganizationKey.fill(0)
        }

    }

    fun getOrganization(orgId: Long, userId: String): OrganizationEntity {

        val organizationEntity = organizationRepo.findById(orgId).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find organization with id ($orgId)") }

        if (isInOrganization(organizationEntity, userId)) {
            return organizationEntity
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this organization ($orgId)")
        }
    }

    fun isInOrganization(organizationEntity: OrganizationEntity, userId: String): Boolean {
        return keycloakService.getGroupsForUser(userId)
            .filter { groupRepresentation ->
                groupRepresentation.id == organizationEntity.keycloakGroupId
            }.size == 1
    }
}