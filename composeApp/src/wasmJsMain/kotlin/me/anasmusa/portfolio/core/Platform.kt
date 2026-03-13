package me.anasmusa.portfolio.core

import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

object Platform {
    val isIOS = hostOs == OS.Ios
    val isAndroid = hostOs == OS.Android

    val isMobile = isAndroid || isIOS
}