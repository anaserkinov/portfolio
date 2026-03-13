package me.anasmusa.portfolio.data.network

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.BaseResponse
import me.anasmusa.portfolio.api.model.EducationResponse
import me.anasmusa.portfolio.api.model.ExperienceResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.SectionType
import me.anasmusa.portfolio.api.model.SkillResponse

object ApiClient {

    private const val BASE_URL = "https://api.anasmusa.me/portfolio"

    private val client = HttpClient(Js){
        install(ContentNegotiation){
            json(Json {
                prettyPrint = true
            })
        }

        defaultRequest {
            url(BASE_URL)
        }
    }

    suspend fun getAbout(): BaseResponse<AboutResponse> {
        return client.get("/${SectionType.About}").body()
    }

    suspend fun getExperience(): BaseResponse<ExperienceResponse> {
        return client.get("/${SectionType.About}").body()
    }

    suspend fun getEducation(): BaseResponse<EducationResponse> {
        return client.get("/${SectionType.About}").body()
    }

    suspend fun getLanguage(): BaseResponse<LanguageResponse> {
        return client.get("/${SectionType.About}").body()
    }

    suspend fun getSkills(): BaseResponse<SkillResponse> {
        return client.get("/${SectionType.About}").body()
    }

    suspend fun getProjects(isPrimary: Boolean? = null): BaseResponse<ProjectResponse> {
        return client.get("/${SectionType.About}"){
            url {
                parameters.append("isPrimary", isPrimary.toString())
            }
        }.body()
    }

}