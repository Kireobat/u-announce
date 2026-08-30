package eu.kireobat.u_announce.api.dto

import eu.kireobat.u_announce.common.AllowedMediaType
import eu.kireobat.u_announce.common.Constants
import jakarta.validation.ValidationException
import java.time.ZonedDateTime

data class ConfirmUploadRequestDto(
    val key: String,                 // S3 media_key returned from pre-signed URL
    val filename: String,            // Display filename (may differ from original)
    val originalFilename: String,    // Actual uploaded filename
    val contentType: String,
    val fileSize: Long,
    val publishTime: ZonedDateTime,
) {
    fun validate() {
        if (!Constants().S3_KEY_REGEX.matches(key)) throw ValidationException("Key does not match pattern ($key)")
        if (fileSize > Constants().MAX_FILE_SIZE) throw ValidationException("File exceeds maximum size (Your file: ${fileSize/1024/1024} MB / Max: ${Constants().MAX_FILE_SIZE/1024/1024} MB)")
        if (!AllowedMediaType.contains(contentType)) throw ValidationException("Content type is not supported/allowed ($contentType)")
        if (publishTime.isBefore(ZonedDateTime.now().minusMinutes(5L))) throw ValidationException("Publish time must be in the future")
    }

}