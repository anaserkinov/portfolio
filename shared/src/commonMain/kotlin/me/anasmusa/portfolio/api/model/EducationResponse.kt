package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class EducationResponse(
    val entities: List<Entity>
) {
    @Serializable
    class Entity(
        val university: String,
        val field: String,
        val completed: String,
        val date: String,
        val position: String,
        val items: List<Item>
    )

    @Serializable
    class Item(
        val value: String
    )
}