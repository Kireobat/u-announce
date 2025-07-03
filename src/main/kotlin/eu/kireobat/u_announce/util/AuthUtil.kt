package eu.kireobat.u_announce.util

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class AuthUtil {

    fun getUserIdFromAuth(): String {
        return getJwt().getClaimAsString("sub")
    }

    fun getUsernameFromAuth(): String {
        return getJwt().getClaimAsString("preferred_username")
    }

    private fun getJwt(): Jwt {
        return when (val authentication = SecurityContextHolder.getContext().authentication) {
            is JwtAuthenticationToken -> {
                authentication.token
            }
            else -> throw RuntimeException("No JWT found in security context")
        }
    }
}