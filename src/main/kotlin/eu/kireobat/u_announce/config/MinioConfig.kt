package eu.kireobat.u_announce.config

import io.minio.admin.MinioAdminClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("minio")
data class MinioProperties(
    var endpoint: String = "",
    var accessKey: String = "",
    var secretKey: String = ""
)

class MinioConfig(private val minioProperties: MinioProperties) {

    @Bean
    fun minioAdminClient(): MinioAdminClient {
        return MinioAdminClient.builder()
            .endpoint(minioProperties.endpoint)
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .build()
    }
}

