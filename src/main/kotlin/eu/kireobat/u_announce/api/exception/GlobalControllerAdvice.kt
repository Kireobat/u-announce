package eu.kireobat.u_announce.api.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import eu.kireobat.u_announce.api.dto.UAnnounceResponseDto
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@ControllerAdvice
class GlobalControllerAdvice {

    private val logger: Logger = LoggerFactory.getLogger(GlobalControllerAdvice::class.java)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<UAnnounceResponseDto> {
        val response = e.message?.let {
            UAnnounceResponseDto(
                false, ZonedDateTime.now(), HttpStatus.BAD_REQUEST, it
            )
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<UAnnounceResponseDto> {
        val response = e.message?.let {
            UAnnounceResponseDto(
                false, ZonedDateTime.now(), HttpStatus.BAD_REQUEST, it
            )
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<UAnnounceResponseDto> {
        logger.warn(e.message)
        val response = e.body.detail?.let {
            UAnnounceResponseDto(
                false, ZonedDateTime.now(), HttpStatus.valueOf(e.body.status), it
            )
        }
        return ResponseEntity.status(HttpStatus.valueOf(e.body.status)).body(response)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(e: AuthorizationDeniedException): ResponseEntity<UAnnounceResponseDto> {
        logger.info(e.message)
        val response = e.message?.let {
            UAnnounceResponseDto(
                false, ZonedDateTime.now(), HttpStatus.UNAUTHORIZED, it
            )
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<UAnnounceResponseDto> {
        val cause = ex.cause
        when {
            cause is InvalidFormatException -> {
                val response = UAnnounceResponseDto(
                    false,
                    ZonedDateTime.now(),
                    HttpStatus.BAD_REQUEST,
                    "Invalid value for field '${cause.message}': ${cause.value}"
                )
                return ResponseEntity.badRequest().body(response)
            }

            else -> {
                val response = UAnnounceResponseDto(
                    false,
                    ZonedDateTime.now(),
                    HttpStatus.BAD_REQUEST,
                    "Invalid request format. Unable to parse request body: ${ex.message}"
                )
                return ResponseEntity.badRequest().body(response)
            }
        }

    }

    @ExceptionHandler(JsonProcessingException::class)
    fun handleJsonProcessingException(ex: JsonProcessingException): ResponseEntity<UAnnounceResponseDto> {
        logger.error("JSON processing error", ex)
        val response = UAnnounceResponseDto(
            false,
            ZonedDateTime.now(),
            HttpStatus.BAD_REQUEST,
            "Invalid JSON format: ${ex.message}"
        )
        return ResponseEntity.badRequest().body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<UAnnounceResponseDto> {
        logger.error("Unhandled exception", e)
        val response = e.message?.let {
            UAnnounceResponseDto(
                false, ZonedDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR, "Server error"
            )
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }
}

