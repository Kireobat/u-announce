package eu.kireobat.u_announce.integration

import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import eu.kireobat.u_announce.common.credentials.Discord
import eu.kireobat.u_announce.persistence.repo.PlatformRepo
import eu.kireobat.u_announce.service.OrganizationService
import eu.kireobat.u_announce.service.SecretService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class DiscordIntegration(
    private val platformRepo: PlatformRepo,
    private val organizationService: OrganizationService,
    private val secretService: SecretService
) {

    private val slug = "discord"

    fun sendMessage(orgId: Long, message2: String, userId: String) {

        val organizationEntity = organizationService.getOrganization(orgId, userId)

        val platformEntity = platformRepo.findBySlug(slug).orElseThrow { throw ResponseStatusException(HttpStatus.NOT_FOUND,"Could not find platform with slug ($slug)") }

        val secretDto = secretService.getSecretByPlatformAndOrg(platformEntity.id, organizationEntity.id, userId)

        require(secretDto.credentials is Discord) { "Platform credentials are not of type Discord" }

        val newDiscordBot = CoroutineScope(Dispatchers.Default).launch {
            val kord = Kord(secretDto.credentials.token)

            kord.on<MessageCreateEvent> { // runs every time a message is created that our bot can read

                // ignore other bots, even ourselves. We only serve humans here!
                if (message.author?.isBot != false) return@on

                // check if our command is being invoked
                if (message.content != "!ping") return@on

                // all clear, give them the pong!
                message.channel.createMessage("pong!")
            }

            kord.login()
        }


    }
}