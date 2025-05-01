package com.anasmusa.portfolio

import org.jetbrains.compose.resources.DrawableResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_en
import portfolio.composeapp.generated.resources.ic_ru
import portfolio.composeapp.generated.resources.ic_uz

enum class Language(val title: String, val icon: DrawableResource, val isoFormat: String){
    ENGLISH("English", Res.drawable.ic_en, "en"),
    UZBEK("O'zbek", Res.drawable.ic_uz, "uz"),
    RUSSIAN("Русский", Res.drawable.ic_ru, "ru")
}
