package eu.kireobat.u_announce.config

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.admin.client.resource.RealmResource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakAdminConfig {

    @Value($$"${keycloak.admin.server-url}")
    private lateinit var serverUrl: String

    @Value($$"${keycloak.admin.realm}")
    private lateinit var realm: String

    @Value($$"${keycloak.admin.client-id}")
    private lateinit var clientId: String

    @Value($$"${keycloak.admin.client-secret}")
    private lateinit var clientSecret: String

    @Bean
    fun keycloakAdminClient(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .grantType("client_credentials")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .build()
    }

    @Bean
    fun uAnnounceRealmResource(): RealmResource {
        return keycloakAdminClient().realm(realm)
    }
}