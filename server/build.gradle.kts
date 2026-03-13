plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
}

group = "me.anasmusa.portfolio.backend"
version = "1.0.0"


application {
    mainClass.set("me.anasmusa.portfolio.ApplicationKt")
}

dependencies {

    implementation(libs.kotlinx.datetime)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.websocket)
    implementation(libs.ktor.server.cors)


    implementation(libs.kotlin.serialization)

    implementation(libs.qdrant)
    implementation(libs.genai)

    implementation(libs.telegram.bot)
    implementation(libs.retrofit)

    implementation(projects.shared)
}