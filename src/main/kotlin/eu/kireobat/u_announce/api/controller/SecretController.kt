package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.api.dto.CreateSecretDto
import eu.kireobat.u_announce.api.dto.SecretDto
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import eu.kireobat.u_announce.service.SecretService
import eu.kireobat.u_announce.util.AuthUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
class SecretController(private val secretService: SecretService) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/secrets")
    fun createSecret(@RequestBody createSecretDto: CreateSecretDto): ResponseEntity<UAnnounceResponseDto> {
        secretService.createSecret(createSecretDto, AuthUtil().getUserIdFromAuth())
        return ResponseEntity.ok(UAnnounceResponseDto(true, ZonedDateTime.now(), HttpStatus.CREATED, "Secret created"))
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/secrets/{secretId}")
    fun getSecret(@PathVariable secretId: Long): ResponseEntity<SecretDto> {
        return ResponseEntity.ok(secretService.getSecretById(secretId, AuthUtil().getUserIdFromAuth()))
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/secrets/{secretId}")
    fun deleteSecret(@PathVariable secretId: Long): ResponseEntity<UAnnounceResponseDto> {
        secretService.deleteSecretById(secretId, AuthUtil().getUserIdFromAuth())
        return ResponseEntity.ok(UAnnounceResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Secret deleted"))
    }
}