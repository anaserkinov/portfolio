package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class MessageResponse(
    val id: Long,
    val message: String
)