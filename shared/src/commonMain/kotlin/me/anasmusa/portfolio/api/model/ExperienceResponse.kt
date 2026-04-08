package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class ExperienceResponse(
    val entities: List<Entity>
) {
    @Serializable
    class Entity(
        val company: String?,
        val link: String?,
        val date: String,
        val position: String,
        val items: List<Item>
    )

    @Serializable
    class Item(
        val value: String
    )
}