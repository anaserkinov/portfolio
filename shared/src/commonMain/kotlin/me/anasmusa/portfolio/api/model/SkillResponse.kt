package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class SkillResponse(
    val proficient: List<List<Entity>>,
    val competent: List<List<Entity>>,
    val familiar: List<List<Entity>>,
) {
    @Serializable
    class Entity(
        val name: String
    )
}