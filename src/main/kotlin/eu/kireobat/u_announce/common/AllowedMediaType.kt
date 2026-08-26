package eu.kireobat.u_announce.common

enum class AllowedMediaType(val mediaType: String) {
    VIDEO_MP4("video/mp4"),
    VIDEO_MPEG("video/mpeg"),
    VIDEO_OGG("video/ogg"),
    VIDEO_WEBM("video/webm"),
    IMAGE_APNG("image/apng"),
    IMAGE_AVIF("image/avif"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_SVG("image/svg"),
    IMAGE_WEBP("image/webp");

    companion object {
        operator fun contains(contentType: String): Boolean {
            return entries.any { it.mediaType.equals(contentType, ignoreCase = true) }
        }

        fun fromContentType(contentType: String): AllowedMediaType? {
            return entries.find { it.mediaType.equals(contentType, ignoreCase = true) }
        }

        fun getAllowedTypes(): List<String> = entries.toTypedArray().map { it.mediaType }
    }
}