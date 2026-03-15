package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class System {
    @SerialName("android") Android,
    @SerialName("compose_multiplatform") ComposeMultiplatform,
    @SerialName("ktor_server") KtorServer,
    @SerialName("react_js") ReactJs,
}