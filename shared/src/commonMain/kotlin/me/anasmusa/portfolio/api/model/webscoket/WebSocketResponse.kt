package me.anasmusa.portfolio.api.model.webscoket
import kotlinx.serialization.Serializable

@Serializable
class WebSocketResponse(
    val type: Int,
    val data: String
)