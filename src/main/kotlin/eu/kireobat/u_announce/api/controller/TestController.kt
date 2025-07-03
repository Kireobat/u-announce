package eu.kireobat.u_announce.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

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
        return "test"
    }
}