package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AboutResponse (
    @SerialName("welcome_message") val welcomeMessage: String,
    val about: String,
) {

    @Serializable
    class Contact(
        val linkedin: String?,
        val github: String?,
        val email: String?,
        val telegram: String?,
        val cv: String?
    )

}