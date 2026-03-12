package me.anasmusa.portfolio.api.model.webscoket
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class WebSocketRequest(
    val type: Int,
    val data: JsonElement?
)