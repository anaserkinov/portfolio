package me.anasmusa.portfolio.chat

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val message: String,
    val type: Type,
    val time: LocalTime
){

    enum class Type{
        USER,
        BOT
    }

}