package eu.kireobat.u_announce.api.dto

import com.github.slugify.Slugify
import io.swagger.v3.oas.annotations.media.Schema

data class CreateOrganizationDto (
    @Schema(description = "Organization name", example = "Arasaka Corporation", required = true)
    val displayName: String
)

fun CreateOrganizationDto.validate() {
    require(displayName.isNotBlank()) { "Organization name must not be blank" }
}

fun CreateOrganizationDto.getSlug(): String {

    val slugify = Slugify.builder()
        .transliterator(true)
        .build()

    return slugify.slugify(this.displayName)
}