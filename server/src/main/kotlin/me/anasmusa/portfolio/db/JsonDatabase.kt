package me.anasmusa.portfolio.db

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import me.anasmusa.portfolio.api.model.*
import me.anasmusa.portfolio.core.AppJson
import java.io.File

object JsonDatabase {

    fun save(json: JsonObject): Boolean {
        return try {
            val folder = File("data")
            folder.mkdirs()
            json.forEach { (key, element) ->
                when(key) {
                    SectionType.About.value -> {
                        AppJson.decodeFromJsonElement<AboutResponse>(element)
                    }
                    SectionType.Experience.value -> {
                        AppJson.decodeFromJsonElement<ExperienceResponse>(element)
                    }
                    SectionType.Education.value -> {
                        AppJson.decodeFromJsonElement<EducationResponse>(element)
                    }
                    SectionType.Language.value -> {
                        AppJson.decodeFromJsonElement<LanguageResponse>(element)
                    }
                    SectionType.Skills.value -> {
                        AppJson.decodeFromJsonElement<SkillResponse>(element)
                    }
                    SectionType.Projects.value -> {
                        AppJson.decodeFromJsonElement<ProjectResponse>(element)
                    }
                    else -> return false
                }

                val file = File(folder, "$key.json")
                file.writeText(AppJson.encodeToString(element))
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    inline fun <reified T> get(type: String): T? {
        return try {
            val folder = File("data")
            folder.mkdirs()
            val file = File(folder, "$type.json")
            if (file.exists()) {
                return AppJson.decodeFromString<T>(file.readText())
            }
            null
        } catch (_: Exception){
            null
        }
    }
}