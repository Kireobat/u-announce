package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.PatchIntegrationStatusDto
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import eu.kireobat.u_announce.api.dto.validate
import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.service.IntegrationStatusService
import eu.kireobat.u_announce.service.OrganizationService
import eu.kireobat.u_announce.util.AuthUtil
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
}