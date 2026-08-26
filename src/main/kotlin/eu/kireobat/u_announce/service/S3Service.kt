package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.api.dto.ConfirmUploadRequestDto
import eu.kireobat.u_announce.api.dto.UploadUrlResponseDto
import eu.kireobat.u_announce.common.AllowedMediaType
import eu.kireobat.u_announce.common.Constants
import eu.kireobat.u_announce.config.RustFSConfig
import eu.kireobat.u_announce.config.S3Properties
import eu.kireobat.u_announce.persistence.entity.S3MetadataEntity
import eu.kireobat.u_announce.persistence.repo.S3MetadataRepo
import eu.kireobat.u_announce.util.AuthUtil
import eu.kireobat.u_announce.util.FormatUtil
import jakarta.ws.rs.ForbiddenException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.time.ZonedDateTime
import java.util.Optional

@Service
class S3Service(private val organizationService: OrganizationService, private val s3MetadataRepo: S3MetadataRepo) {

    private val logger = LoggerFactory.getLogger(S3Service::class.java)

    @Value($$"${s3.url}")
    private lateinit var serverUrl: String

    @Value($$"${s3.access-key}")
    private lateinit var accessKey: String

    @Value($$"${s3.secret-key}")
    private lateinit var secretKey: String

    private val rustFSAdminClient: S3Client by lazy {
        val rustFSProperties = S3Properties(serverUrl, accessKey, secretKey)

        RustFSConfig(rustFSProperties).rustFSAdminClient()
    }

    private val rustFSPresigner: S3Presigner by lazy {
        val rustFSProperties = S3Properties(serverUrl, accessKey, secretKey)

        RustFSConfig(rustFSProperties).rustFsPresigner()
    }

    fun createUploadUrl(
        orgId: Long,
        contentType: String,
        expectedSize: Long,
        displayName: String?
    ): UploadUrlResponseDto {
        if (expectedSize > Constants().MAX_FILE_SIZE) {
            throw IllegalArgumentException("File exceeds maximum allowed size")
        }

        if (expectedSize <= 0) {
            throw IllegalArgumentException("Invalid file size")
        }

        if (!AllowedMediaType.contains(contentType)) {
            throw IllegalArgumentException("Invalid content type")
        }


        val organizationEntity = organizationService.getOrganization(orgId, AuthUtil().getUserIdFromAuth())


        val extension = getFileExtension(contentType)
        val key = "pending" +
                "/${organizationEntity.slug}" +
                "/${FormatUtil().generateBucketTimestamp()}-${FormatUtil().generateRandomUrlSafeBase64(4)}" +
                "/${FormatUtil().generateRandomUrlSafeBase64(32)}.$extension"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(Constants().BUCKET_NAME)
            .key(key)
            .contentType(contentType)
            .contentLength(expectedSize)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofHours(1))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedRequest: PresignedPutObjectRequest =
            rustFSPresigner.presignPutObject(presignRequest)


        return UploadUrlResponseDto(
            presignedRequest.url().toString(),
            "PUT",
            key,
            ZonedDateTime.ofInstant(presignedRequest.expiration(), Constants().DEFAULT_TIMEZONE),
            Constants().MAX_FILE_SIZE
        )
    }

    fun confirmUpload(confirmUploadRequestDto: ConfirmUploadRequestDto): S3MetadataEntity {

        if (!fileExists(confirmUploadRequestDto.key)) {
            throw ForbiddenException("File not found in storage. Upload may have failed or you may not have access.")
        }

        val organizationEntity = organizationService.getOrganization(extractSlugFromKey(confirmUploadRequestDto.key),
            AuthUtil().getUserIdFromAuth())

        return s3MetadataRepo.saveAndFlush(S3MetadataEntity().apply {
            mediaKey = confirmUploadRequestDto.key
            displayFilename = confirmUploadRequestDto.filename
            originalFilename = confirmUploadRequestDto.filename
            organizationId = organizationEntity.id
            keycloakCreatedByUserId = AuthUtil().getUserIdFromAuth()
            publishTime = confirmUploadRequestDto.publishTime
            expiryTime = confirmUploadRequestDto.publishTime.plusDays(Constants().EXPIRY_TIME)
        })


    }

    fun fileExists(key: String): Boolean {
        try {
            rustFSAdminClient.headObject {
                it.bucket(Constants().BUCKET_NAME)
                    .key(key)
            }
            return true
        } catch (e: NoSuchKeyException) {
            return false
        }
    }

    private fun getFileExtension(contentType: String): String = when (contentType.lowercase()) {
        "video/mp4" -> "mp4"
        "image/jpeg", "image/jpg" -> "jpg"
        "image/png" -> "png"
        "image/gif" -> "gif"
        else -> "bin"
    }

    private fun extractSlugFromKey(key: String): String {
        return key.split("/").getOrNull(1) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Improper key format")
    }
}