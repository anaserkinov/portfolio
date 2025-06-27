package me.anasmusa.portfolio

import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.JsString

external class Rive {
    fun resizeDrawingSurfaceToCanvas()
    fun play()
}

@JsFun("rive => rive.stateMachineInputs('State Machine 1')")
external fun getRiveInputs(rive: Rive): JsArray<JsAny>

@JsFun("input => input.name")
external fun getInputName(input: JsAny): JsString

@JsFun("(input, value) => input.value = value")
external fun setInputValue(input: JsAny, value: JsBoolean)

@JsName("riveInstance")
external val riveInstance: Rive

object BotAnimation {

    val canvas = window.document.getElementById("rive-canvas") as? HTMLCanvasElement

    lateinit var hoverInput: JsAny
    lateinit var clickInput: JsAny
    lateinit var thinkInput: JsAny

    fun resize() {
        riveInstance.resizeDrawingSurfaceToCanvas()
    }

    fun play(){
        riveInstance.play()
    }


    fun init() {
        canvas?.style?.visibility = "visible"

        val inputs = getRiveInputs(riveInstance)
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
    }

    fun enterHover() {
        setInputValue(hoverInput, true.toJsBoolean())
    }
    fun exitHover() {
        setInputValue(hoverInput, false.toJsBoolean())
    }
    fun click(value: Boolean) {
        setInputValue(clickInput, value.toJsBoolean())
    }
    fun think(value: Boolean) {
        setInputValue(thinkInput, value.toJsBoolean())
    }

    fun update(
        size: Int,
        start: Int,
        top: Int
    ){
        canvas?.style?.let {
            it.height = "${size}px"
            it.width = "${size}px"
            it.top = "${top}px"
            it.left = "${start}px"
        }
    }

}