package eu.kireobat.u_announce.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var issuerUri: String

    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "keycloak-oauth2"

        return OpenAPI()
            .info(
                Info()
                    .title("uAnnounce API")
                    .description("Universal Announcement Manager API")
                    .version("0.0.1-SNAPSHOT")
                    .license(
                        License()
                            .name("GNU General Public License v3.0")
                            .url("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    )
            )
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(
                Components()
                    .addSecuritySchemes(securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.OAUTH2)
                            .flows(
                                OAuthFlows()
                                    // We use the Authorization Code flow for Swagger UI.
                                    .authorizationCode(
                                        OAuthFlow()
                                            // The URLs are derived from the issuer-uri.
                                            .authorizationUrl("$issuerUri/protocol/openid-connect/auth")
                                            .tokenUrl("$issuerUri/protocol/openid-connect/token")
                                            .scopes(null) // Scopes can be defined here if needed
                                    )
                            )
                    )
            )
    }
}
