package eu.kireobat.u_announce.service;

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.getSlug
import eu.kireobat.u_announce.common.Constants.Companion.KEYCLOAK_REALM
import eu.kireobat.u_announce.common.Constants.Companion.KEYCLOAK_ROLE_ORGANIZATION_ADMIN
import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.persistence.repo.OrganizationRepo
import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.idm.GroupRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OrganizationService(
    keycloak: Keycloak,
    private val organizationRepo: OrganizationRepo,
) {

    @Value($$"${keycloak.admin.client-id}")
    private lateinit var clientId: String

    private val realm = keycloak.realm(KEYCLOAK_REALM)

    fun createOrganization(createOrganizationDto: CreateOrganizationDto, userId: String) {

        val generatedSlug = createOrganizationDto.getSlug()

        val groupRepresentation = GroupRepresentation()

        groupRepresentation.name = "org-$generatedSlug"

        val response = realm.groups().add(groupRepresentation)

        if (response.status != 201) {
            throw RuntimeException("Failed to create Keycloak group. Status: ${response.statusInfo.reasonPhrase}")
        }

        val newGroupId = response.location.path.split("/").last()
        realm.users().get(userId).joinGroup(newGroupId)

        val clientRepresentation = realm.clients().findByClientId(clientId).first()

        val orgAdminRole = realm.clients().get(clientRepresentation.id).roles().get(KEYCLOAK_ROLE_ORGANIZATION_ADMIN).toRepresentation()

        realm.users().get(userId).roles().clientLevel(clientRepresentation.id).add(listOf(orgAdminRole))

        organizationRepo.saveAndFlush(OrganizationEntity().apply {
            slug = generatedSlug
            displayName = createOrganizationDto.displayName
            keycloakGroupId = newGroupId
            keycloakCreatedByUserId = userId
        })
    }
}