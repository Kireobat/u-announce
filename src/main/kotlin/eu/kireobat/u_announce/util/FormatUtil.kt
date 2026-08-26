package eu.kireobat.u_announce.util

import java.time.ZonedDateTime
import kotlin.random.Random


class FormatUtil {

    fun generateBucketTimestamp(): String {
        val time = ZonedDateTime.now()

        return """
            ${time.year}
            ${time.month.toString().padStart(2, '0')}
            ${time.dayOfMonth.toString().padStart(2, '0')}
            _
            ${time.hour.toString().padStart(2, '0')}
            ${time.minute.toString().padStart(2, '0')}
            ${time.second.toString().padStart(2, '0')}
            """
    }

    fun generateRandomUrlSafeBase64(finalLength: Number): String {

        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'

        var randString = ""

        for (index in finalLength.toInt() downTo(0)) {
            randString += charPool[Random.nextInt(0, charPool.size)]
        }

        return randString
    }
}