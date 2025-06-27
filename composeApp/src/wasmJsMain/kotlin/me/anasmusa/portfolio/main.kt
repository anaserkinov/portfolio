package me.anasmusa.portfolio

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.get

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    (window.document.getElementById("loader") as? HTMLDivElement)?.style?.visibility = "hidden"
    ComposeViewport(document.body!!) {
        App()
    }
}