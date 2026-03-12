package me.anasmusa.portfolio

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private val token = System.getenv("LOGBASE_BOT_TOKEN")
private val chatId = System.getenv("LOGBASE_CHAT_ID")

fun log(message: String){
    try {
        val text = URLEncoder.encode("#portfolio\n $message", StandardCharsets.UTF_8)
        ProcessBuilder(
            "curl",
            "-X", "GET",
            "https://api.telegram.org/bot$token/sendMessage?chat_id=$chatId&text=$text"
        ).redirectErrorStream(true)
            .start()
            .waitFor()
    } catch (e: Exception){
        e.printStackTrace()
    }
}