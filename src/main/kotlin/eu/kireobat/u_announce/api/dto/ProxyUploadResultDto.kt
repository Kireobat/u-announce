package eu.kireobat.u_announce.api.dto

data class ProxyUploadResultDto(
    var success: Boolean = false,
    var message: String = "",
    var statusCode: Int = 500,
    var timingMs: Long = 0,
    var key: String = "",
    var verified: Boolean = false,
    var error: String? = null
)

