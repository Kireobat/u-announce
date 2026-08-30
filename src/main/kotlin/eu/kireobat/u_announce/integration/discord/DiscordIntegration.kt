package eu.kireobat.u_announce.integration.discord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import eu.kireobat.u_announce.common.credentials.Discord
import eu.kireobat.u_announce.persistence.repo.IntegrationStatusRepo
import eu.kireobat.u_announce.service.SecretService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.ConcurrentHashMap

@Component
class DiscordIntegration(
    private val integrationStatusRepo: IntegrationStatusRepo,
    private val secretService: SecretService
) {
    private val activeKordInstances = ConcurrentHashMap<String, Kord>()

    private val logger: Logger = LoggerFactory.getLogger(DiscordIntegration::class.java)

    suspend fun getOrCreateKordInstance(orgId: Long, platformId: Long, userId: String): Kord {
        val instanceKey = "orgId-$orgId--platformId-$platformId"

        return activeKordInstances[instanceKey] ?: run {
            val integrationStatus = integrationStatusRepo.findByOrganizationIdAndPlatformIdAndActiveTrue(orgId, platformId)
            require(integrationStatus.isPresent) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Discord is not active for your organization")
            }

            val secretDto = secretService.getSecretByPlatformAndOrg(platformId, orgId, userId)
            require(secretDto.credentials is Discord) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Credentials with id (${secretDto.id}) do not match requirements for Discord")
            }

            logger.info("Creating new Kord instance for orgId: $orgId, platformId: $platformId")

            try {
                val kord = Kord(secretDto.credentials.token)
                // Don't call login here - it's automatic when you create message channels
                activeKordInstances[instanceKey] = kord
                logger.info("Kord instance created and stored successfully")
                kord
            } catch (e: Exception) {
                logger.error("Failed to create Kord instance", e)
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initialize Discord bot", e)
            }
        }
    }

    suspend fun sendMessage(orgId: Long, platformId: Long, userId: String, channelId: String, message: String): Boolean {
        return try {
            val kord = getOrCreateKordInstance(orgId, platformId, userId)
            logger.info("Kord instance established")

            val channel = kord.getChannelOf<TextChannel>(Snowflake(channelId))
            logger.info("Channel established: ${channel?.name}")

            channel?.createMessage(message)
            logger.info("Message sent successfully")
            true
        } catch (e: ResponseStatusException) {
            logger.error("Discord API error: ${e.message}")
            throw e
        } catch (e: Exception) {
            logger.error("Failed to send Discord message", e)
            false
        }
    }

    suspend fun sendMessageToChannelByName(orgId: Long, platformId: Long, userId: String, guildId: String, channelName: String, message: String): Boolean {
        return try {
            val kord = getOrCreateKordInstance(orgId, platformId, userId)

            val guild = kord.getGuildOrNull(Snowflake(guildId))
            val channel = guild?.channels?.firstOrNull {
                it.name == channelName && it is TextChannel
            } as? TextChannel
            channel?.createMessage(message)
            true
        } catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }

    fun shutdownAll() {
        runBlocking {
            activeKordInstances.values.forEach { kord ->
                try {
                    kord.shutdown()
                } catch (e: Exception) {
                    logger.error(e.message)
                    // Log error if needed
                }
            }
            activeKordInstances.clear()
        }
    }

    fun shutdownBot(instanceKey: String) {
        runBlocking {
            activeKordInstances.remove(instanceKey)?.shutdown()
        }
    }
}