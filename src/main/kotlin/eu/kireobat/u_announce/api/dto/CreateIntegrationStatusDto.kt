package eu.kireobat.u_announce.api.dto

data class CreateIntegrationStatusDto(
    val organizationId: Long,
    val platformId: Long,
    val active: Boolean
)
