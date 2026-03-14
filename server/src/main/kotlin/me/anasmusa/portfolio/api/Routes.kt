package me.anasmusa.portfolio.api

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.BaseResponse
import me.anasmusa.portfolio.api.model.EducationResponse
import me.anasmusa.portfolio.api.model.ExperienceResponse
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.SectionType
import me.anasmusa.portfolio.api.model.SkillResponse
import me.anasmusa.portfolio.api.model.webscoket.setupWebSocket
import me.anasmusa.portfolio.bot.Bot
import me.anasmusa.portfolio.core.Config
import me.anasmusa.portfolio.db.JsonDatabase
import java.io.File

fun Application.module() {
    install(CORS){
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            explicitNulls = false
        })
    }

    setupWebSocket()

    routing {
        post("/bot"){
            if (call.request.headers["X-Telegram-Bot-Api-Secret-Token"] != Config.ADMIN_BOT_SECRET_TOKEN) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            Bot.processUpdate(call.receiveText())
            call.respond(HttpStatusCode.OK)
        }

        get("/download/{path...}"){
            val filePath = call.parameters.getAll("path")!!
            val folder = File("data")
            val file = File(folder, filePath.joinToString("/"))
            call.respondFile(file)
        }

        get("/{type}"){
            when(val type = call.pathParameters["type"]!!){
                SectionType.About.value -> {
                    JsonDatabase.get<AboutResponse>(type)?.let {
                        return@get call.respond(BaseResponse(it))
                    }
                }
                SectionType.Experience.value -> {
                    JsonDatabase.get<ExperienceResponse>(type)?.let {
                        return@get call.respond(BaseResponse(it))
                    }
                }
                SectionType.Education.value -> {
                    JsonDatabase.get<EducationResponse>(type)?.let {
                        return@get call.respond(BaseResponse(it))
                    }
                }
                SectionType.Language.value -> {
                    JsonDatabase.get<LanguageResponse>(type)?.let {
                        return@get call.respond(BaseResponse(it))
                    }
                }
                SectionType.Skills.value -> {
                    JsonDatabase.get<SkillResponse>(type)?.let {
                        return@get call.respond(BaseResponse(it))
                    }
                }
                SectionType.Projects.value -> {
                    var response = JsonDatabase.get<ProjectResponse>(type) ?:
                    return@get call.respond(BaseResponse(null))

                    val isPrimary = call.queryParameters["isPrimary"]?.toBoolean()
                    if (isPrimary == true)
                        response = response.copy(entities = response.entities.filter { it.isPrimary })
                    return@get call.respond(BaseResponse(response))
                }
            }
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

