package me.anasmusa.portfolio.api.model.webscoket

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.close
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import me.anasmusa.portfolio.ai.AI
import me.anasmusa.portfolio.api.model.webscoket.message.MessageRequest
import me.anasmusa.portfolio.api.model.webscoket.message.MessageResponse
import me.anasmusa.portfolio.db.QdrantDatabase
import me.anasmusa.portfolio.core.log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.set
import kotlin.text.toLong
import kotlin.time.Duration.Companion.seconds

private var userId = AtomicLong(1L)
private val activeSessions = ConcurrentHashMap<Long, DefaultWebSocketServerSession>()
private val requestQueue = Channel<QueuedRequest>(Channel.UNLIMITED)
private val messageIdByUser = HashMap<Long, Long>()

fun Application.setupWebSocket() {
    install(WebSockets){
        pingPeriod = 15.seconds
        timeout = 30.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
        contentConverter = KotlinxWebsocketSerializationConverter(
            Json {
                prettyPrint = true
                explicitNulls = false
            }
        )
    }

    launch {
        startWebSocket()
    }

    routing {
        webSocket("/ws"){
            webSocket(userId.incrementAndGet())
        }
        webSocket("/ws/{userId}"){
            webSocket(
                call.parameters["userId"]?.toLong() ?: userId.incrementAndGet()
            )
        }
    }
}

private suspend fun startWebSocket(){
    for (request in requestQueue){
        if (request.messageId != (messageIdByUser[request.userId] ?: 0L))
            continue
        try {
            val context = QdrantDatabase.find(
                AI.embedText(request.message)
            )
            val aiResponse = AI.generate(
                request.message,
                context,
                request.history
            )

            log("Message: ${request.message}\n" + "Response: $aiResponse")

            activeSessions[request.userId]?.apply {
                sendSerialized(
                    WebSocketResponse(
                        WebsocketEntityType.MESSAGE,
                        Json.encodeToJsonElement(MessageResponse(request.messageId, aiResponse))
                    )
                )
            }
        } catch (e: Exception){
            e.printStackTrace()
            log("Message: ${request.message}\n" + "Error: ${e.message}")

            activeSessions[request.userId]?.apply {
                sendSerialized(
                    WebSocketResponse(
                        WebsocketEntityType.MESSAGE,
                        Json.encodeToJsonElement(
                            MessageResponse(
                                request.messageId,
                                "Unknown error"
                            )
                        )
                    )
                )
            }
        }
        delay(1000)
    }
}

private suspend fun DefaultWebSocketServerSession.webSocket(userId: Long){
    activeSessions[userId] = this

    try {
        while (isActive){
            val request = receiveDeserialized<WebSocketRequest>()
            if (request.type == WebsocketEntityType.MESSAGE){
                val messageRequest = Json.decodeFromJsonElement<MessageRequest>(request.data!!)
                messageIdByUser[userId] = messageRequest.id
                requestQueue.send(
                    QueuedRequest(
                        userId,
                        messageRequest.id,
                        messageRequest.message,
                        messageRequest.history
                    )
                )
            } else if (request.type == WebsocketEntityType.ID){
                sendSerialized(
                    WebSocketResponse(
                        WebsocketEntityType.ID,
                        JsonPrimitive("$userId")
                    )
                )
            }
        }
    } catch (e: Exception){
        e.printStackTrace()
        this.close()
    } finally {
        activeSessions.remove(userId)
    }
}
