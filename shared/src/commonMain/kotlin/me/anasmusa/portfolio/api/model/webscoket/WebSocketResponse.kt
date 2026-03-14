package me.anasmusa.portfolio.api.model.webscoket

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
class WebSocketResponse(
    val type: Int,
    val data: JsonElement
)