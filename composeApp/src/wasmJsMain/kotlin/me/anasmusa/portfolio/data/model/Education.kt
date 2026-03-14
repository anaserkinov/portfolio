package me.anasmusa.portfolio.data.model


class Education(
    val entities: List<Entity>
) {
    class Entity(
        val university: String,
        val field: String,
        val completed: String,
        val date: String,
        val position: String,
        val items: List<String>
    )
}