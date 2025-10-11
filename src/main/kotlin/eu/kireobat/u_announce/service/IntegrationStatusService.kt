package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.api.dto.CreateIntegrationStatusDto
import eu.kireobat.u_announce.api.dto.PatchIntegrationStatusDto
import eu.kireobat.u_announce.persistence.entity.IntegrationStatusEntity
import eu.kireobat.u_announce.persistence.repo.IntegrationStatusRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

@Service
class IntegrationStatusService(
    private val organizationService: OrganizationService,
    private val integrationStatusRepo: IntegrationStatusRepo
) {
    fun setupIntegrationStatus(createIntegrationStatusDto: CreateIntegrationStatusDto, userId: String) {
        val organizationEntity = organizationService.getOrganization(createIntegrationStatusDto.organizationId, userId)

        integrationStatusRepo.saveAndFlush(IntegrationStatusEntity(
            organizationId = createIntegrationStatusDto.organizationId,
            platformId = createIntegrationStatusDto.platformId,
            active = createIntegrationStatusDto.active,
            keycloakCreatedByUserId = userId
        ))
    }

    fun patchIntegrationStatus(patchIntegrationStatusDto: PatchIntegrationStatusDto, userId: String) {

        val integrationStatusEntity = integrationStatusRepo.findById(patchIntegrationStatusDto.id).orElseThrow { throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Could not find integrationStatus with id (${patchIntegrationStatusDto.id})") }

        val organizationEntity = organizationService.getOrganization(integrationStatusEntity.organizationId, userId)

        if (patchIntegrationStatusDto.organizationId != null && patchIntegrationStatusDto.organizationId != organizationEntity.id) {
            val newOrganizationEntity = organizationService.getOrganization(patchIntegrationStatusDto.organizationId, userId)

            integrationStatusEntity.apply {
                organizationId = newOrganizationEntity.id
            }
        }

        integrationStatusEntity.apply {
            platformId = patchIntegrationStatusDto.platformId ?: platformId
            active = patchIntegrationStatusDto.active ?: active
        }
    }

    fun getIntegrationStatus(orgId: Long, platformId: Long, userId: String): Optional<IntegrationStatusEntity> {
        val organizationEntity = organizationService.getOrganization(orgId, userId)

        return integrationStatusRepo.findByOrganizationIdAndPlatformId(organizationEntity.id, platformId)
    }
}