package me.anasmusa.portfolio.data.model

import me.anasmusa.portfolio.api.model.Link
import me.anasmusa.portfolio.api.model.Platform
import me.anasmusa.portfolio.api.model.ProjectResponse

data class Project(
    val logoPath: String,
    val title: String,
    val platforms: List<Platform>,
    val techStack: String?,
    val date: String,
    val description: Description,
    val isWhiteLabel: Boolean,
    val links: List<Link>,
    val isPrimary: Boolean
){
    data class Description(
        val short: String,
        val items: List<String>
    )

    data class PlatformInfo(
        val platform: Platform,
        val count: Int
    )
}

fun ProjectResponse.toUI(): List<Project> {
    return entities.map { it.toUI() }
}

fun ProjectResponse.Entity.toUI(): Project {
    return Project(
        logoPath = logoPath,
        title = title,
        platforms = platforms,
        techStack = techStack
            .takeUnless { it.isEmpty() }?.joinToString("   ") { "#$it" },
        date = date,
        description = description.toUI(),
        isWhiteLabel = isWhiteLabel,
        links = links,
        isPrimary = isPrimary
    )
}

fun ProjectResponse.Description.toUI(): Project.Description {
    return Project.Description(
        short = short,
        items = items.map { it.value },
    )
}

fun ProjectResponse.PlatformInfo.toUI(): Project.PlatformInfo {
    return Project.PlatformInfo(
        platform = platform,
        count = count
    )
}