package eu.kireobat.u_announce.api.dto

data class PatchIntegrationStatusDto(
    val id: Long,
    val organizationId: Long? = null,
    val platformId: Long? = null,
    val active: Boolean? = null
)
