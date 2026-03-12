package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class LanguageResponse(
    val entities: List<Entity>
) {
    @Serializable
    class Entity(
        val code: String,
        val name: Int,
        val level: String
    )
}