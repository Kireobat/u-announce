package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.PatchIntegrationStatusDto
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import eu.kireobat.u_announce.api.dto.validate
import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.service.IntegrationStatusService
import eu.kireobat.u_announce.service.OrganizationService
import eu.kireobat.u_announce.util.AuthUtil
import eu.kireobat.u_announce.persistence.entity.IntegrationStatusEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
class IntegrationStatusController(private val integrationStatusService: IntegrationStatusService) {

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/integrations")
    fun patchIntegrationStatus(@RequestBody patchIntegrationStatusDto: PatchIntegrationStatusDto): ResponseEntity<UAnnounceResponseDto> {

        // validate

        integrationStatusService.patchIntegrationStatus(patchIntegrationStatusDto, AuthUtil().getUserIdFromAuth())

        return ResponseEntity.ok(UAnnounceResponseDto(true, ZonedDateTime.now(), HttpStatus.CREATED, "Integration status patched"))
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/integrations/org/{orgId}/platform/{platformId}")
    fun getIntegrationStatus(@PathVariable orgId: Long, @PathVariable platformId: Long): ResponseEntity<IntegrationStatusEntity> {

        val integrationStatusEntity = integrationStatusService.getIntegrationStatus(orgId, platformId, AuthUtil().getUserIdFromAuth()).orElseThrow { 
            ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find integrationStatus for orgId ($orgId) and platformId ($platformId)")
        }

        return ResponseEntity.ok(integrationStatusEntity)
    }
}