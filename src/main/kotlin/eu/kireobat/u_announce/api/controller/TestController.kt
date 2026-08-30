package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.integration.discord.DiscordIntegration
import eu.kireobat.u_announce.util.AuthUtil
import kotlinx.coroutines.runBlocking
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class TestController(private val discordIntegration: DiscordIntegration) {
    @GetMapping(path = ["/test"])
    fun test(): String {
        return "test"
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = ["/testWithAuth"])
    fun testWithAuth(): String {
        return AuthUtil().getUsernameFromAuth()
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/discord")
    fun sendMessage(
        @RequestParam orgId: Long,
        @RequestParam platformId: Long,
        @RequestParam channelId: String,
        @RequestParam message: String
        ): Boolean {
        return runBlocking {
            discordIntegration.sendMessage(orgId,platformId, AuthUtil().getUserIdFromAuth(),channelId,message)
        }
    }
}