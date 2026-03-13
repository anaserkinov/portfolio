package me.anasmusa.portfolio.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.BaseResponse
import me.anasmusa.portfolio.api.model.EducationResponse
import me.anasmusa.portfolio.api.model.ExperienceResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.SectionType
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.api.model.webscoket.setupWebSocket
import me.anasmusa.portfolio.db.JsonDatabase
import java.io.File

fun Application.module() {
    install(CORS){
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    setupWebSocket()

    routing {
        get("/download/{path}"){
            val filePath = call.parameters["path"]!!
            val folder = File("data")
            val file = File(folder, filePath)
            call.respondBytes(file.readBytes(), contentType = ContentType.fromFilePath(filePath).firstOrNull())
        }

        get("/portfolio/{type}"){
            val response = when(val type = call.parameters["type"]!!){
                SectionType.About.value -> JsonDatabase.get<AboutResponse>(type)
                SectionType.Experience.value -> JsonDatabase.get<ExperienceResponse>(type)
                SectionType.Education.value -> JsonDatabase.get<EducationResponse>(type)
                SectionType.Language.value -> JsonDatabase.get<LanguageResponse>(type)
                SectionType.Skills.value -> JsonDatabase.get<SkillResponse>(type)
                SectionType.Projects.value -> JsonDatabase.get<ProjectResponse>(type)
                else -> {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
            }
            if (response == null)
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(
                    BaseResponse(response)
                )
        }
    }
}

