package me.anasmusa.portfolio.chat

import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class ChatState(
    val loading: Boolean = true,
    val message: String = "",
    val messages: List<Message> = emptyList(),
    val waitingForResponse: Boolean = false
)

sealed class Intent{
    data object Start: Intent()
    data class UpdateMessage(val message: String): Intent()
    data object Send: Intent()
    data class AddMessage(val message: Message): Intent()
}

sealed class Event {
    data class OnNewMessage(val message: Message): Event()
}

class ViewModel {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState>
        get() = _state

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events: Flow<Event> = _events.receiveAsFlow()

    private var lastMessageId = 0L

    fun handle(intent: Intent){
        when(intent){
            Intent.Start -> start()

            is Intent.UpdateMessage -> {
                _state.update { it.copy(message = intent.message) }
            }

            Intent.Send -> send()

            is Intent.AddMessage -> {
                _state.update {
                    it.copy(
                        messages = arrayListOf(intent.message).also {
                            it.addAll(state.value.messages)
                        }
                    )
                }
            }
        }
    }

    private val socket = Socket(
        onInitialized = {
            scope.launch {
                val messages = localStorage.getItem("messages")?.let {
                    Json.decodeFromString<List<Message>>(it)
                }
                lastMessageId = messages?.firstOrNull()?.id ?: 0L
                _state.update {
                    it.copy(
                        messages = messages ?: emptyList(),
                        loading = false
                    )
                }
            }
        },
        onGetNewMessage = {
            scope.launch {
                _state.update { state ->
                    state.copy(
                        waitingForResponse = false
                    )
                }
                _events.send(Event.OnNewMessage(it))
            }
            scope.launch {
                localStorage.setItem(
                    "messages",
                    Json.encodeToString(
                        arrayListOf(it).also {
                            it.addAll(state.value.messages)
                        }
                    )
                )
            }
        }
    )
    private fun start(){
        socket.start(scope)
    }

    private fun send(){
        val messageId = ++lastMessageId
        val message = state.value.message.removeSuffix("\n")
        socket.send(
            ++lastMessageId,
            message,
            state.value.messages
        )
        _state.update { state ->
            state.copy(
                message = "",
                messages = arrayListOf(
                    Message(
                        messageId,
                        state.message,
                        Message.Type.USER,
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time)
                ).also { it.addAll(state.messages) },
                waitingForResponse = true
            )
        }
    }

    fun onClear(){
        socket.close()
        scope.cancel()
    }
}