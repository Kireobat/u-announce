package eu.kireobat.u_announce.api.dto

import eu.kireobat.u_announce.common.credentials.Credentials

data class SecretDto(
    val id: Long,
    val orgId: Long,
    val platformId: Long,
    val credentials: Credentials
)
