package eu.kireobat.u_announce.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
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
    }
}
