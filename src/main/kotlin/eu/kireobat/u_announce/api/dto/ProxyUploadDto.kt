package eu.kireobat.u_announce.api.dto

data class ProxyUploadDto(
    val uploadUrl: String,
    val method: String,
    val key: String,
    val expiresAt: String,
    val maxFileSize: Long
)
