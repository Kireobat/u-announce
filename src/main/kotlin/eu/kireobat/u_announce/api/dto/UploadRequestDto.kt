package eu.kireobat.u_announce.api.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.ZonedDateTime

data class UploadRequestDto (
    @Schema(example = "1", required = true)
    val orgId: Long,
    @Schema(example = "image/jpeg", required = true)
    val contentType: String,
    @Schema(example = "5321352", required = true)
    val expectedSize: Long, // filesize in bytes
    @Schema(example = "myImage.jpg", required = true)
    val displayName: String,
    @Schema(example = "2026-08-26T23:59:59.999Z", required = true)
    val publishTime: ZonedDateTime
)