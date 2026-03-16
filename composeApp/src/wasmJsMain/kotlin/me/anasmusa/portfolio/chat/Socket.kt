package me.anasmusa.portfolio.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import me.anasmusa.portfolio.api.model.webscoket.WebSocketRequest
import me.anasmusa.portfolio.api.model.webscoket.WebSocketResponse
import me.anasmusa.portfolio.api.model.webscoket.WebsocketEntityType
import me.anasmusa.portfolio.api.model.webscoket.message.MessageRequest
import me.anasmusa.portfolio.api.model.webscoket.message.MessageResponse
import org.w3c.dom.WebSocket
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class Socket(
    private val onInitialized: () -> Unit,
    private val onGetNewMessage: (message: Message) -> Unit
) {

    private var socket: WebSocket? = null
    private var userId = -1L

    @OptIn(ExperimentalWasmJsInterop::class)
    private fun createSocket() {
        if (socket == null)
            socket = WebSocket(
                if (userId == -1L)
                    "wss://api.anasmusa.me/portfolio/ws"
                else
                    "wss://api.anasmusa.me/portfolio/ws/$userId"
            )
    }

    @OptIn(ExperimentalTime::class, ExperimentalWasmJsInterop::class)
    fun start(scope: CoroutineScope) {
        createSocket()

        socket!!.onopen = {
            if (userId == -1L)
                loadUserId()
        }

        socket!!.onmessage = { event ->
            try {
                val response =
                    Json.decodeFromString<WebSocketResponse>((event.data as? JsString).toString())
                println(response)
                if (response.type == WebsocketEntityType.MESSAGE) {
                    val message = Json.decodeFromJsonElement<MessageResponse>(response.data)
                    onGetNewMessage(
                        Message(
                            message.id,
                            message.message.removeSuffix("\n"),
                            Message.Type.BOT,
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
                        )
                    )
                } else if (response.type == WebsocketEntityType.ID) {
                    userId = response.data.jsonPrimitive.long
                    onInitialized()
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }

        scope.launch {
            while (true) {
                if (socket?.readyState != WebSocket.OPEN && socket?.readyState != WebSocket.CONNECTING) {
                    socket = null
                    createSocket()
                }
                delay(2000)
            }
        }
    }

    fun loadUserId() {
        if (socket?.readyState == WebSocket.OPEN)
            socket?.send(
                Json.encodeToString(
                    WebSocketRequest(WebsocketEntityType.ID, null)
                )
            )
    }

    fun send(
        messageId: Long,
        message: String,
        messages: List<Message>
    ) {
        if (socket?.readyState == WebSocket.OPEN)
            socket?.send(
                Json.encodeToString(
                    WebSocketRequest(
                        WebsocketEntityType.MESSAGE,
                        Json.encodeToJsonElement(
                            MessageRequest(
                                id = messageId,
                                message = message,
                                history = arrayListOf<MessageRequest.QA>().also {
                                    let { _ ->
                                        var question: String? = null
                                        var answer: String? = null
                                        messages.forEach { message ->
                                            if (message.type == Message.Type.BOT) {
                                                answer = message.message
                                                question = null
                                            } else if (message.type == Message.Type.USER)
                                                question = message.message

                                            if (question != null && answer != null) {
                                                it.add(0, MessageRequest.QA(question, answer))
                                                question = null
                                                answer = null
                                                if (it.size == 5)
                                                    return@let
                                            }
                                        }
                                    }
                                }
                            )
                        )
                    )
                )
            )
    }

    fun close(){
        socket?.close()
    }

}