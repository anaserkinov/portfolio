package me.anasmusa.portfolio.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Link(
    val type: LinkType,
    val value: String,
)

@Serializable
enum class LinkType {
    @SerialName("linked_in") LinkedIn,
    @SerialName("github") Github,
    @SerialName("mail") Mail,
    @SerialName("telegram") Telegram,
    @SerialName("cv") CV,
    @SerialName("google_play") GooglePlay,
    @SerialName("website") Website,
}