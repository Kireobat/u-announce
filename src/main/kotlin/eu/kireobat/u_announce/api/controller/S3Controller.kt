package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.api.dto.ConfirmUploadRequestDto
import eu.kireobat.u_announce.api.dto.ProxyUploadDto
import eu.kireobat.u_announce.api.dto.ProxyUploadResultDto
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import eu.kireobat.u_announce.api.dto.UploadRequestDto
import eu.kireobat.u_announce.api.dto.UploadUrlResponseDto
import eu.kireobat.u_announce.persistence.entity.S3MetadataEntity
import eu.kireobat.u_announce.service.OrganizationService
import eu.kireobat.u_announce.service.S3Service
import io.swagger.v3.oas.annotations.Parameter
import jakarta.transaction.Transactional
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotFoundException
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.net.URI
import java.time.ZonedDateTime

@RestController
@RequestMapping("api/v1")
class S3Controller(
    private val s3Service: S3Service,
    private val organizationService: OrganizationService,
    private val restTemplate: RestTemplate
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/s3/request-upload")
    fun requestUpload(@RequestBody uploadRequestDto: UploadRequestDto): ResponseEntity<UploadUrlResponseDto> {

        return ResponseEntity.ok(
            s3Service.createUploadUrl(
                uploadRequestDto.orgId,
                uploadRequestDto.contentType,
                uploadRequestDto.expectedSize,
                uploadRequestDto.displayName,
            )
        )

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/s3/confirm-upload")
    @Transactional
    fun confirmUpload(@RequestBody confirmUploadRequestDto: ConfirmUploadRequestDto): ResponseEntity<S3MetadataEntity> {

        confirmUploadRequestDto.validate()

        return ResponseEntity.ok(s3Service.confirmUpload(confirmUploadRequestDto))
    }

    @Profile("local", "dev")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/s3/proxy-upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun proxyUpload(
        @Parameter(description = "Upload URL from /s3/request-upload", required = true)
        @RequestParam("uploadUrl") uploadUrl: String,

        @Parameter(description = "HTTP method", required = true)
        @RequestParam(value = "method", defaultValue = "PUT") method: String,

        @Parameter(description = "S3 object key", required = true)
        @RequestParam("key") key: String,

        @Parameter(description = "URL expiration timestamp", required = true)
        @RequestParam("expiresAt") expiresAt: String,

        @Parameter(description = "Maximum file size in bytes", required = true)
        @RequestParam("maxFileSize") maxFileSize: Long,

        @Parameter(description = "File to upload", required = true)
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<ProxyUploadResultDto> {

        val startTime = System.currentTimeMillis()

        val proxyUploadDto = ProxyUploadDto(
            uploadUrl,
            method,
            key,
            expiresAt,
            maxFileSize
        )

        try {
            val uploadResponse = uploadToUrl(
                uploadUrl,
                file.inputStream.buffered(),
                file.contentType ?: "application/octet-stream",
                file.size
            )

            val totalTime = System.currentTimeMillis() - startTime

            val fileExists = s3Service.fileExists(proxyUploadDto.key)

            return ResponseEntity.ok(ProxyUploadResultDto().apply {
                this.success = fileExists && uploadResponse.statusCode.is2xxSuccessful
                this.message = if (fileExists) "Upload successful" else "Upload succeeded but file not found"
                this.statusCode = uploadResponse.statusCode.value()
                this.timingMs = totalTime
                this.key = proxyUploadDto.key
                this.verified = fileExists
            })
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed: "+e.message)
        }
    }


    fun uploadToUrl(
        uploadUrl: String,
        inputStream: BufferedInputStream,
        contentType: String,
        fileSize: Long
    ): ResponseEntity<String> {
        val headers = HttpHeaders().apply {
            this.contentType = MediaType.valueOf(contentType)
            this.contentLength = fileSize
        }

        val body = inputStream.readBytes()

        val requestEntity = org.springframework.http.RequestEntity(
            body,
            headers,
            HttpMethod.PUT,
            URI.create(uploadUrl)
        )

        return restTemplate.exchange<String>(requestEntity)
    }
}