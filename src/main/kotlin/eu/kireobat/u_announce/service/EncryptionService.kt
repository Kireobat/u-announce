package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.config.EncryptionProperties
import org.springframework.stereotype.Service
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

@Service
class EncryptionService(encryptionProperties: EncryptionProperties) {

    private val masterKeySpec: SecretKeySpec

    private val secureRandom: SecureRandom = SecureRandom()

    private val ALGORITHM = "AES/GCM/NoPadding"
    private val IV_LENGTH_BYTES = 12
    private val TAG_LENGTH_BITS = 128

    init {
        val decodedKey = Base64.decode(encryptionProperties.masterKey)
        this.masterKeySpec = SecretKeySpec(decodedKey, "AES")
    }

    /**
     * Encrypts the given plaintext using the application's master key or another key if specified.
     * The output contains the IV (Initialization Vector) prefixed to the ciphertext.
     */
    fun encrypt(plaintext: ByteArray, overrideSecretKeySpec: SecretKeySpec? = null): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)

        // Generate a random, non-repeating Initialization Vector (IV).
        // It is critical that the IV is never reused for the same key with GCM.
        val iv = ByteArray(IV_LENGTH_BYTES)
        secureRandom.nextBytes(iv) // Use a secure random source to generate the IV.

        val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.ENCRYPT_MODE, overrideSecretKeySpec ?: masterKeySpec, gcmParameterSpec)

        val ciphertext = cipher.doFinal(plaintext)

        // Prepend the IV to the ciphertext. We need the IV for decryption.
        return iv + ciphertext
    }

    /**
     * Decrypts the given ciphertext using the application's master key or another key if specified.
     * Assumes the IV is prepended to the ciphertext.
     */
    fun decrypt(fullCiphertext: ByteArray, overrideSecretKeySpec: SecretKeySpec? = null): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)

        // Extract the IV from the beginning of the ciphertext.
        val iv = fullCiphertext.copyOfRange(0, IV_LENGTH_BYTES)
        val ciphertext = fullCiphertext.copyOfRange(IV_LENGTH_BYTES, fullCiphertext.size)

        val gcmParameterSpec = GCMParameterSpec(TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, overrideSecretKeySpec ?: masterKeySpec, gcmParameterSpec)

        return cipher.doFinal(ciphertext)
    }
}