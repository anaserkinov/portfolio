package com.anasmusa.portfolio.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val lightScheme = lightColorScheme(
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF000000),
    secondary = Color(0xFF6B6B6B),
    surface = Color(0xFFEDEDED),
    surfaceBright = Color(0xFFCDCDCD),
    surfaceContainer = Color(0xFFF5F5F5),
    onSurface = Color(0xFF505050),
    primary = Color(0xFFF4B465),
)

val darkScheme = darkColorScheme(
    background = Color(0xFF151515),
    onBackground = Color(0xFFFFFFFF),
    secondary = Color(0xFFCCCCCC),
    surface = Color(0xFF2E2E2E),
    surfaceBright = Color(0xFF717171),
    surfaceContainer = Color(0xFF151515),
    onSurface = Color(0xFFFFFFFF),
    primary = Color(0xFFFFEA9F)
)