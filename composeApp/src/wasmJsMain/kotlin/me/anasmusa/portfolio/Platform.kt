package me.anasmusa.portfolio

import org.jetbrains.skiko.OS

object Platform {
    val isIOS = org.jetbrains.skiko.hostOs == OS.Ios
    val isAndroid = org.jetbrains.skiko.hostOs == OS.Android

    val isMobile = isAndroid || isIOS
}