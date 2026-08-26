package eu.kireobat.u_announce.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
data class RustFSProperties(
    var url: String = "",
    var accessKey: String = "",
    var secretKey: String = ""
)

// https://rustfs.org/developer/sdk/java

class RustFSConfig(private val rustFSProperties: RustFSProperties) {

    @Bean
    fun rustFSAdminClient(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(rustFSProperties.url))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        rustFSProperties.accessKey,
                        rustFSProperties.secretKey)
                )
            )
            .forcePathStyle(true)
            .build()
    }
}

@Configuration
class RustFSAdminConfig {

    @Value($$"${rustfs.url}")
    private lateinit var serverUrl: String

    @Value($$"${rustfs.access-key}")
    private lateinit var accessKey: String

    @Value($$"${rustfs.secret-key}")
    private lateinit var secretKey: String

    @Bean
    fun rustFSAdminClient(): S3Client {
        return S3Client.builder()
            .endpointOverride(URI.create(serverUrl))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        accessKey,
                        secretKey)
                )
            )
            .forcePathStyle(true)
            .build()
    }
}