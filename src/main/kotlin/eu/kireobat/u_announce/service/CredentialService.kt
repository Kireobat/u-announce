package eu.kireobat.u_announce.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import eu.kireobat.u_announce.common.credentials.Credentials
import eu.kireobat.u_announce.persistence.entity.PlatformEntity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CredentialService {

    private val objectMapper = jacksonObjectMapper()

    fun validateAndSerialize(platformEntity: PlatformEntity, rawCredentials: Map<String, Any>): String {
        try {
            val credentialClass = Class.forName(platformEntity.className).kotlin

            val validatedObject = objectMapper.convertValue(rawCredentials, credentialClass.java)

            return objectMapper.writeValueAsString(validatedObject)

        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("Credential class '${platformEntity.className}' not found for platform '${platformEntity.slug}'", e)
        } catch (e: IllegalArgumentException) {
            // Catch other potential errors like JSON parsing exceptions
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid credentials structure for '${platformEntity.slug}'. Error: ${e.message}"
            )
        }
    }

    fun validateAndDeserialize(platformEntity: PlatformEntity, rawCredentials: String): Credentials {
        try {
            val credentialClass = Class.forName(platformEntity.className).kotlin

            val deserializedObject = objectMapper.readValue(rawCredentials, credentialClass.java)

            return deserializedObject as Credentials
        } catch (e: ClassNotFoundException) {
            throw IllegalStateException("Credential class '${platformEntity.className}' not found for platform '${platformEntity.slug}'", e)
        }
    }
}
