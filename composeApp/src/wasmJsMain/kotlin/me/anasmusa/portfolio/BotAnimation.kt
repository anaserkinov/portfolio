package me.anasmusa.portfolio

import kotlinx.browser.window
import kotlinx.coroutines.Runnable
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.js.JsString

@OptIn(ExperimentalWasmJsInterop::class)
external class Rive {
    fun resizeDrawingSurfaceToCanvas()
    fun play()
    fun stateMachineInputs(value: JsString): JsArray<JsAny>
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("input => input.name")
external fun getInputName(input: JsAny): JsString

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(input, value) => input.value = value")
external fun setInputValue(input: JsAny, value: JsBoolean)

@JsName("riveInstance")
external val riveInstance: Rive


@OptIn(ExperimentalWasmJsInterop::class)
object BotAnimation {

    private var canvas: HTMLCanvasElement? = null

    private var hoverInput: JsAny? = null
    private var clickInput: JsAny? = null
    private var thinkInput: JsAny? = null

    private var initialized = false
    private val jobs = ArrayList<Runnable>()

    init {
        init()
    }

    fun init(){
        waitForCanvas {
            canvas?.style?.visibility = "visible"

            val inputs = riveInstance.stateMachineInputs("State Machine 1".toJsString())
            val hoverName = "Hover?".toJsString()
            val clickName = "Click?".toJsString()
            val laughName = "Click 2".toJsString()
            val length = inputs.length
            for (i in 0 until length){
                val input = inputs[i]!!
                if (getInputName(input) == hoverName) {
                    hoverInput = input
                } else if (getInputName(input) == clickName) {
                    clickInput = input
                } else if (getInputName(input) == laughName) {
                    thinkInput = input
                }
            }

            riveInstance.play()

            jobs.forEach { it.run() }
            jobs.clear()
        }
    }

    private fun findCanvas(): HTMLCanvasElement? =
        window.document.body?.shadowRoot?.getElementById("rive-canvas") as? HTMLCanvasElement

    @OptIn(ExperimentalWasmJsInterop::class)
    private fun waitForCanvas(callback: () -> Unit) {
        var interval = 0
        interval = window.setInterval({
            canvas = findCanvas()
            if (canvas != null) {
                initialized = true
                window.clearInterval(interval)
                callback()
            }
            null
        }, 200)
    }

    fun resize() {
        if (initialized)
            riveInstance.resizeDrawingSurfaceToCanvas()
    }

    fun enterHover() {
        hoverInput?.let {
            setInputValue(it, true.toJsBoolean())
        }
    }
    fun exitHover() {
        hoverInput?.let {
            setInputValue(it, false.toJsBoolean())
        }
    }
    fun click(value: Boolean) {
        clickInput?.let {
            setInputValue(it, value.toJsBoolean())
        }
    }
    fun think(value: Boolean) {
        thinkInput?.let {
            setInputValue(it, value.toJsBoolean())
        }
    }

    fun update(
        size: Int,
        start: Int,
        top: Int
    ){
        if (initialized)
            canvas?.style?.let {
                it.height = "${size}px"
                it.width = "${size}px"
                it.top = "${top}px"
                it.left = "${start}px"
            }
        else
            jobs.add(
                Runnable {
                    canvas?.style?.let {
                        it.height = "${size}px"
                        it.width = "${size}px"
                        it.top = "${top}px"
                        it.left = "${start}px"
                    }
                }
            )
    }

}