package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Platform {
    @SerialName("android") Android,
    @SerialName("ios") Ios,
    @SerialName("desktop") Desktop,
    @SerialName("web") Web,
    @SerialName("backend") Backend,
}