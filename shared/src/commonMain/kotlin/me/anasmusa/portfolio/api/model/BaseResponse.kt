package me.anasmusa.portfolio.api.model

import kotlinx.serialization.Serializable

@Serializable
class BaseResponse<out T>(
    val data: T
)