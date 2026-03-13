package me.anasmusa.portfolio.core

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun log(message: String){
    try {
        val text = URLEncoder.encode("#portfolio\n $message", StandardCharsets.UTF_8)
        ProcessBuilder(
            "curl",
            "-X", "GET",
            "https://api.telegram.org/bot${Config.ADMIN_BOT_TOKEN}/sendMessage?chat_id=${Config.ADMIN_TG_ID}&text=$text"
        ).redirectErrorStream(true)
            .start()
            .waitFor()
    } catch (e: Exception){
        e.printStackTrace()
    }
}