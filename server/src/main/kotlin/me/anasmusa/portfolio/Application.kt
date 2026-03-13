package me.anasmusa.portfolio

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.anasmusa.portfolio.api.module
import me.anasmusa.portfolio.bot.Bot
import me.anasmusa.portfolio.db.QdrantDatabase

suspend fun main() {
    QdrantDatabase.init()
    Bot.start()

    embeddedServer(
        Netty,
        port = 8085,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}
