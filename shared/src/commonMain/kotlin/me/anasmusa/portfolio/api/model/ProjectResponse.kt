package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val entities: List<Entity>
){

    @Serializable
    class Entity(
        val logoUrl: String,
        val title: String,
        val systems: List<System>,
        val date: String,
        val description: Description,
        @SerialName("is_white_label") val isWhiteLabel: Boolean,
        val links: List<Link>,
        @SerialName("is_primary") val isPrimary: Boolean
    )

    @Serializable
    class Description(
        val short: String,
        val items: List<Item>
    ) {
        @Serializable
        class Item(
            val value: String
        )
    }

}