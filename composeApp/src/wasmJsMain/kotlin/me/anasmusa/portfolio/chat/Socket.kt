package me.anasmusa.portfolio.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.w3c.dom.WebSocket

@Serializable
class SocketRequest(
    val type: Int,
    val data: JsonElement?
)

@Serializable
data class SocketResponse(
    val type: Int,
    val data: JsonElement
)

private const val TYPE_MESSAGE = 1
private const val TYPE_ID = 2

class Socket(
    private val onInitialized: () -> Unit,
    private val onGetNewMessage: (message: Message) -> Unit
) {

    private var socket: WebSocket? = null
    private var userId = -1L

    private fun createSocket() {
        if (socket == null)
            socket = WebSocket(
                if (userId == -1L)
                    "wss://api.anasmusa.me/portfolio/ws"
                else
                    "wss://api.anasmusa.me/portfolio/ws/$userId"
            )
    }

    fun start(scope: CoroutineScope) {
        createSocket()

        socket!!.onopen = {
            if (userId == -1L)
                loadUserId()
        }

        socket!!.onmessage = { event ->
            try {
                val response =
                    Json.decodeFromString<SocketResponse>((event.data as? JsString).toString())
                println(response)
                if (response.type == TYPE_MESSAGE) {
                    val message = Json.decodeFromString<MessageResponse>(
                        (response.data as JsonPrimitive).content
                    )
                    onGetNewMessage(
                        Message(
                            message.id,
                            message.message.removeSuffix("\n"),
                            Message.Type.BOT,
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
                        )
                    )
                } else if (response.type == TYPE_ID) {
                    userId = (response.data as JsonPrimitive).content.toLong()
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
                    SocketRequest(TYPE_ID, null)
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
                    SocketRequest(
                        TYPE_MESSAGE,
                        Json.encodeToJsonElement(
                            MessageRequest(
                                messageId,
                                message,
                                arrayListOf<MessageRequest.QA>().also {
                                    let { _->
                                        var question: String? = null
                                        var answer: String? = null
                                        messages.forEach { message ->
                                            if (message.type == Message.Type.BOT) {
                                                answer = message.message
                                                question = null
                                            }else if (message.type == Message.Type.USER)
                                                question = message.message

                                            print("q: $question, \n a:$answer")

                                            if (question != null && answer != null){
                                                it.add(0, MessageRequest.QA(question, answer))
                                                question = null
                                                answer = null
                                                if (it.size == 5)
                                                    return@let
                                            }
                                        }
                                    }
                                    print("History: $it")
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