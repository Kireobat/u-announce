package eu.kireobat.u_announce.api.dto

data class PatchIntegrationStatusDto(
    val id: Long,
    val organizationId: Long?,
    val platformId: Long?,
    val active: Boolean?
)
