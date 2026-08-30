package eu.kireobat.u_announce.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random


class FormatUtil {

    fun generateBucketTimestamp(): String = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

    fun generateRandomUrlSafeBase64(finalLength: Int): String {

        require(finalLength > 0) { "Length must be positive" }

        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'

        return buildString(finalLength) {
            repeat(finalLength) {
                append(charPool.random())
            }
        }
    }
}