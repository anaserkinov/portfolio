package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val entities: List<Entity>,
    val totalCount: Int = entities.size,
    val platforms: List<PlatformInfo> = emptyList()
){

    @Serializable
    data class Entity(
        @SerialName("logo_path") val logoPath: String,
        val title: String,
        val platforms: List<Platform>,
        @SerialName("tech_stack") val techStack: List<String>,
        val date: String,
        val description: Description,
        @SerialName("is_white_label") val isWhiteLabel: Boolean,
        val links: List<Link>,
        @SerialName("is_primary") val isPrimary: Boolean
    )

    @Serializable
    data class Description(
        val short: String,
        val items: List<Item>
    ) {
        @Serializable
        data class Item(
            val value: String
        )
    }

    @Serializable
    data class PlatformInfo(
        val platform: Platform,
        val count: Int
    )

}