package eu.kireobat.u_announce.api.dto

data class CreateSecretDto(
    val orgId: Long,
    val platformId: Long,
    val credentials: Map<String, Any>
)
