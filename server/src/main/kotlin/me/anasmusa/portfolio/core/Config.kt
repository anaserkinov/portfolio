package me.anasmusa.portfolio.core

object Config {

    val ADMIN_BOT_TOKEN: String by lazy { System.getenv("ADMIN_BOT_TOKEN") }
    val ADMIN_BOT_WEBHOOK_URL: String = System.getenv("ADMIN_BOT_WEBHOOK_URL")
    val ADMIN_BOT_SECRET_TOKEN: String = System.getenv("ADMIN_BOT_SECRET_TOKEN")
    val ADMIN_TG_ID: Long by lazy { System.getenv("ADMIN_TG_ID").toLong() }

}