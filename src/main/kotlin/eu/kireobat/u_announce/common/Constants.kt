package eu.kireobat.u_announce.common

import java.time.ZoneId
import java.util.TimeZone

class Constants {
    val MAX_FILE_SIZE: Long = 500 * 1024 * 1024 // max size (MB) converted to bytes
    val DEFAULT_TIMEZONE: ZoneId = ZoneId.of("UTC")
    val ENCRYPTION_ALGO: String = "AES"
    val BUCKET_NAME: String = "u-announce-media"
    val EXPIRY_TIME: Long = 30L
    val S3_KEY_REGEX: Regex = Regex("(active|pending)/[a-z-0-9]+/[0-9]{8}_[0-9]{6}-[a-zA-Z0-9_-]{4}/[a-zA-Z0-9_-]{32}\\.[a-zA-Z]+")
}