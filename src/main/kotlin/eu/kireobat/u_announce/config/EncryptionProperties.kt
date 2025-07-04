package eu.kireobat.u_announce.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import jakarta.validation.constraints.NotBlank

@Validated
@ConfigurationProperties(prefix = "u-announce.security")
data class EncryptionProperties(
    /**
     * The Base64 encoded 256-bit master key for the application.
     * This is loaded from the UANNOUNCE_MASTER_KEY environment variable.
     */
    @NotBlank
    val masterKey: String
)
