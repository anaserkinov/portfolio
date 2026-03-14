package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AboutResponse (
    @SerialName("first_name") val firstName: String,
    @SerialName("photo_url") val photoUrl: String,
    @SerialName("welcome_message") val welcomeMessage: String,
    val about: String,
    val contact: List<Link>
)