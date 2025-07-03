package eu.kireobat.u_announce.service;

import eu.kireobat.u_announce.api.dto.CreateOrganizationDto
import eu.kireobat.u_announce.api.dto.getSlug
import eu.kireobat.u_announce.api.dto.validate
import eu.kireobat.u_announce.persistence.entity.OrganizationEntity
import eu.kireobat.u_announce.persistence.repo.OrganizationRepo
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.representations.idm.GroupRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class OrganizationService(
    private val realm: RealmResource,
    private val organizationRepo: OrganizationRepo,
) {

    @Value($$"${roles.org-admin}")
    private lateinit var orgAdmin: String

    fun createOrganization(createOrganizationDto: CreateOrganizationDto, userId: String) {

        createOrganizationDto.validate()

        require(organizationRepo.findByDisplayName(createOrganizationDto.displayName).isEmpty) { "Organization name must be unique" }

        val generatedSlug = createOrganizationDto.getSlug()

        val groupRepresentation = GroupRepresentation()

        groupRepresentation.name = "org-$generatedSlug"

        val response = realm.groups().add(groupRepresentation)

        if (response.status != 201) {
            throw RuntimeException("Failed to create Keycloak group. Status: ${response.statusInfo.reasonPhrase}")
        }

        val newGroupId = response.location.path.split("/").last()
        realm.users().get(userId).joinGroup(newGroupId)

        val orgAdminRole = realm.roles().get(orgAdmin).toRepresentation()
        realm.users().get(userId).roles().realmLevel().add(listOf(orgAdminRole))

        organizationRepo.saveAndFlush(OrganizationEntity().apply {
            slug = generatedSlug
            displayName = createOrganizationDto.displayName
            keycloakGroupId = newGroupId
            keycloakCreatedByUserId = userId
        })
    }
}