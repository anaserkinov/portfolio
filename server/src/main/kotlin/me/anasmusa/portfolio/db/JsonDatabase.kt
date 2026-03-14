package me.anasmusa.portfolio.db

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.EducationResponse
import me.anasmusa.portfolio.api.model.ExperienceResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.SectionType
import me.anasmusa.portfolio.api.model.SkillResponse
import java.io.File

object JsonDatabase {

    fun save(json: JsonObject): Boolean {
        return try {
            val folder = File("data")
            folder.mkdirs()
            json.forEach { (key, element) ->
                when(key) {
                    SectionType.About.value -> {
                        Json.decodeFromJsonElement<AboutResponse>(element)
                    }
                    SectionType.Experience.value -> {
                        Json.decodeFromJsonElement<ExperienceResponse>(element)
                    }
                    SectionType.Education.value -> {
                        Json.decodeFromJsonElement<EducationResponse>(element)
                    }
                    SectionType.Language.value -> {
                        Json.decodeFromJsonElement<LanguageResponse>(element)
                    }
                    SectionType.Skills.value -> {
                        Json.decodeFromJsonElement<SkillResponse>(element)
                    }
                    SectionType.Projects.value -> {
                        Json.decodeFromJsonElement<ProjectResponse>(element)
                    }
                    else -> return false
                }

                val file = File(folder, "$key.json")
                file.writeText(Json.encodeToString(element))
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
                return Json.decodeFromString<T>(file.readText())
            }
            null
        } catch (_: Exception){
            null
        }
    }
}