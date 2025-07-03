package eu.kireobat.u_announce.api.controller

import eu.kireobat.u_announce.util.AuthUtil
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class TestController {
    @GetMapping(path = ["/test"])
    fun test(): String {
        return "test"
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = ["/testWithAuth"])
    fun testWithAuth(): String {
        return AuthUtil().getUsernameFromAuth()
    }
}