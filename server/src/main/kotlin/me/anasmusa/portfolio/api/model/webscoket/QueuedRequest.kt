package me.anasmusa.portfolio.api.model.webscoket

import me.anasmusa.portfolio.api.model.webscoket.message.MessageRequest

data class QueuedRequest(
    val userId: Long,
    val messageId: Long,
    val message: String,
    val history: List<MessageRequest.QA>
)