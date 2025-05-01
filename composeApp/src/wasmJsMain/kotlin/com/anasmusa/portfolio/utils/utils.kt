package com.anasmusa.portfolio.utils

import androidx.compose.runtime.Composable
import com.anasmusa.portfolio.LocalWindowSize

@Composable
fun isTablet() = LocalWindowSize.current.width >= 768

@Composable
fun select(portrait: Int, landscape: Int) = if (isTablet()) landscape else portrait