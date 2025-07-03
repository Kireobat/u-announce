package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import eu.kireobat.u_announce.api.dto.validate
import eu.kireobat.u_announce.service.OrganizationService
import eu.kireobat.u_announce.util.AuthUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/organizations")
    fun createOrganization(@RequestBody createOrganizationDto: CreateOrganizationDto): ResponseEntity<UAnnounceResponseDto> {

        createOrganizationDto.validate()

        organizationService.createOrganization(createOrganizationDto, AuthUtil().getUserIdFromAuth())

        return ResponseEntity.ok(UAnnounceResponseDto(true, ZonedDateTime.now(), HttpStatus.CREATED, "Organization created"))
    }


}