package me.anasmusa.portfolio.api.model.webscoket.message

import kotlinx.serialization.Serializable

@Serializable
class MessageResponse(
    val id: Long,
    val message: String
)