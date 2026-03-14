package me.anasmusa.portfolio.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.browser.localStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class ChatState(
    val loading: Boolean = true,
    val message: String = "",
    val messages: List<Message> = emptyList(),
    val waitingForResponse: Boolean = false
)

sealed interface ChatIntent{
    data object Start: ChatIntent
    data class UpdateMessage(val message: String): ChatIntent
    data object Send: ChatIntent
    data class AddMessage(val message: Message): ChatIntent
}

sealed interface ChatEvent {
    data class OnNewMessage(val message: Message): ChatEvent
}

class ChatViewModel: ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState>
        get() = _state

    private val _events = Channel<ChatEvent>(Channel.BUFFERED)
    val events: Flow<ChatEvent> = _events.receiveAsFlow()

    private var lastMessageId = 0L

    fun handle(intent: ChatIntent){
        when(intent){
            ChatIntent.Start -> start()

            is ChatIntent.UpdateMessage -> {
                _state.update { it.copy(message = intent.message) }
            }

            ChatIntent.Send -> send()

            is ChatIntent.AddMessage -> {
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
            viewModelScope.launch {
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
            viewModelScope.launch {
                _state.update { state ->
                    state.copy(
                        waitingForResponse = false
                    )
                }
                _events.send(ChatEvent.OnNewMessage(it))
            }
            viewModelScope.launch {
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
        socket.start(viewModelScope)
    }

    @OptIn(ExperimentalTime::class)
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
    }
}