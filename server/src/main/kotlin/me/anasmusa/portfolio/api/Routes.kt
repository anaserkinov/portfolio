package me.anasmusa.portfolio.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import me.anasmusa.portfolio.api.model.webscoket.setupWebSocket

fun Application.module() {
    install(CORS){
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    setupWebSocket()
}

