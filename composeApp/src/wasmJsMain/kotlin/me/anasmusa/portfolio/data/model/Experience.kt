package me.anasmusa.portfolio.data.model

class Experience(
    val entities: List<Entity>
) {
    class Entity(
        val company: String?,
        val link: String?,
        val date: String,
        val position: String,
        val items: List<String>
    )
}