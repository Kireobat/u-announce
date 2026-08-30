package eu.kireobat.u_announce.service

import eu.kireobat.u_announce.config.KeycloakConfig
import eu.kireobat.u_announce.config.KeycloakProperties
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.GroupRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class KeycloakService() {

    private val logger = LoggerFactory.getLogger(KeycloakService::class.java)

    @Value($$"${keycloak.admin.server-url}")
    private lateinit var serverUrl: String

    @Value($$"${keycloak.admin.realm}")
    private lateinit var realm: String

    @Value($$"${keycloak.admin.client-id}")
    private lateinit var clientId: String

    @Value($$"${keycloak.admin.client-secret}")
    private lateinit var clientSecret: String

    @Value($$"${roles.org-admin}")
    private lateinit var orgAdmin: String

    private val keycloakAdminClient: RealmResource by lazy {
        val keycloakProperties = KeycloakProperties(serverUrl, realm, clientId, clientSecret)

        KeycloakConfig(keycloakProperties).keycloakAdminClient().realm(realm)
    }

    fun getGroupsForUser(userId: String): List<GroupRepresentation> {
        return try {
            val groups = keycloakAdminClient.users().get(userId).groups()
            logger.info("Successfully retrieved groups for user ($userId)")
            groups
        } catch (e: Exception) {
            logger.error("Failed to retrieved groups for user ($userId)", e)
            throw e
        }
    }

    fun createOrganization(orgSlug: String, userId: String): Optional<String> {
        return try {

            val optKeycloakGroupId = createOrg(orgSlug)

            require(optKeycloakGroupId.isPresent) { "Could not get ID for newly created org ($orgSlug)" }

            joinOrg(optKeycloakGroupId.get(), userId)

            val optOrgAdminRole = getRole(orgAdmin)

            require(optOrgAdminRole.isPresent) { "Could not get role ($orgAdmin)" }

            giveRealmLevelRoles(listOf(optOrgAdminRole.get()), userId)

            logger.info("Successfully created new org ($orgSlug)")

            optKeycloakGroupId
        } catch (e: Exception) {
            logger.error("Failed to create organization ($orgSlug)", e)
            Optional.empty()
        }
    }

    fun getRole(roleName: String): Optional<RoleRepresentation> {
        return try {
            val role = keycloakAdminClient.roles().get(roleName).toRepresentation()
            logger.info("Successfully retrieved role ($roleName)")
            Optional.of(role)
        } catch (e: Exception) {
            logger.error("Failed to retrieve role ($roleName)", e)
            Optional.empty()
        }
    }

    private fun giveRealmLevelRoles(roleList: List<RoleRepresentation>, userId: String): Boolean {
        return try {
            keycloakAdminClient.users().get(userId).roles().realmLevel().add(roleList)

            logger.info("Successfully gave user ($userId) roles")
            true
        } catch (e: Exception) {
            logger.error("Failed to give user ($userId) roles", e)
            false
        }
    }

    private fun joinOrg(orgId: String, userId: String): Boolean {
        return try {
            keycloakAdminClient.users().get(userId).joinGroup(orgId)

            logger.info("Successfully joined user ($userId) to org ($orgId)")
            true
        } catch (e: Exception) {
            logger.error("Failed to join user ($userId) to org ($orgId)", e)
            false
        }
    }


    private fun createOrg(orgSlug: String): Optional<String> {

        return try {
            val orgName = "org-$orgSlug"

            val groupRepresentation = GroupRepresentation().apply {
                name = orgName
            }

            val response = keycloakAdminClient.groups().add(groupRepresentation)

            if (response.status != 201) {
                throw RuntimeException("Failed to create Keycloak group. Status: ${response.statusInfo.reasonPhrase}")
            }

            logger.info("Successfully created Keycloak group ($orgName)")
            Optional.of(response.location.path.split("/").last())
        } catch (e: Exception) {
            logger.error("Failed to create Keycloak group ($orgSlug)", e)
            Optional.empty()
        }
    }
}