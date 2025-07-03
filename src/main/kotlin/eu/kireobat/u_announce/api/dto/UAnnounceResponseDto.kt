package eu.kireobat.u_announce.api.dto

import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

data class UAnnounceResponseDto(
    var success: Boolean,
    var timestamp: ZonedDateTime,
    var status: HttpStatus,
    var message: String
)