package me.anasmusa.portfolio.core

import kotlinx.serialization.json.Json

val AppJson = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
}