package me.anasmusa.portfolio.chat

import kotlinx.serialization.Serializable

@Serializable
class MessageResponse(
    val id: Long,
    val message: String
)

@Serializable
class MessageRequest(
    val id: Long,
    val message: String,
    val history: List<QA> = emptyList()
){
    @Serializable
    class QA(
        val question: String,
        val answer: String
    )
}