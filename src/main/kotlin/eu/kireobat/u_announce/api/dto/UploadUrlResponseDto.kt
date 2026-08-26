package eu.kireobat.u_announce.api.dto

import java.time.ZonedDateTime

data class UploadUrlResponseDto(
    val uploadUrl: String,
    val method: String,
    val key: String,
    val expiresAt: ZonedDateTime,
    val maxFileSize: Long
)
