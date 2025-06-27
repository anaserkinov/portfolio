package me.anasmusa.portfolio.chat

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.anasmusa.portfolio.BotAnimation
import me.anasmusa.portfolio.Platform
import me.anasmusa.portfolio.component.BouncingDots
import me.anasmusa.portfolio.core.select
import org.w3c.dom.events.Event
import kotlin.math.min


external interface VisualViewport {
    val height: Double
    fun addEventListener(type: String, callback: (Event) -> Unit)
    fun removeEventListener(type: String, callback: (Event) -> Unit)
}

@JsName("visualViewport")
external val visualViewport: VisualViewport

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BoxWithConstraintsScope.Chat(
    isDarkTheme: Boolean,
    onStateChanged: (expanded: Boolean) -> Unit
) {
    val viewModel = remember { ViewModel() }
    val state by viewModel.state.collectAsState()

    var riveInitialized = remember { false }
    var newMessage by remember { mutableStateOf<Message?>(null) }
    var typedMessage by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    if (!riveInitialized) {
        BotAnimation.init()
        BotAnimation.play()
        riveInitialized = true
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect {
            when (it) {
                is me.anasmusa.portfolio.chat.Event.OnNewMessage -> {
                    focusRequester.requestFocus()
                    newMessage = it.message
                }
            }
        }
    }

    LaunchedEffect(newMessage) {
        if (newMessage != null){
            val builder = StringBuilder()
            var index = 0
            while (newMessage != null){
                builder.append(newMessage!!.message[index++])
                typedMessage = builder.toString()
                if (index == newMessage!!.message.length) {
                    BotAnimation.think(false)
                    viewModel.handle(Intent.AddMessage(newMessage!!))
                    newMessage = null
                }else
                    delay(20)
            }
        }
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.onClear()
        }
    }

    var expanded by remember { mutableStateOf(false) }

    val transition = updateTransition(targetState = expanded, label = "BoxTransition")

    val fabSize = select(56, 64)
    val padding = select(24, 32)

    val width by transition.animateDp(
        transitionSpec = { tween(500) },
        label = "Width"
    ) {
        if (it)
            if (maxHeight > maxWidth) maxWidth else min(maxWidth, 480.dp)
        else
            fabSize.dp
    }

    val height by transition.animateDp(
        transitionSpec = { tween(500) },
        label = "Height"
    ) { if (it) (maxHeight.value - 2 * padding).dp else fabSize.dp }

    val alpha by transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "Alpha"
    ) { if (it) 1f else 0f }

    LaunchedEffect(expanded) {
        BotAnimation.resize()
        onStateChanged(expanded)
        if (expanded)
            viewModel.handle(Intent.Start)
    }

    let {
        val maxHeight = this@Chat.maxHeight.value.toInt()
        val maxWidth = this@Chat.maxWidth.value.toInt()

        val expandedBotSize = select(340, 380)
        val botSize by transition.animateInt(
            transitionSpec = { tween(500) },
            label = "BotSize"
        ) { if (it) expandedBotSize else select(240, 280) }

        val botStart by transition.animateInt(
            transitionSpec = { tween(500) },
            label = "BotStart"
        ) {
            if (it) {
                if (maxHeight > maxWidth) {
                    (maxWidth - expandedBotSize) / 2
                } else {
                    val width = min(maxWidth, 480)
                    val start = maxWidth - width - padding
                    start + (width - expandedBotSize) / 2
                }
            } else
                maxWidth - botSize - padding + select(92, 108)
        }

        val botTop by transition.animateInt(
            transitionSpec = { tween(500) },
            label = "BotTop"
        ) {
            if (it)
                -select(6, 4) / 2 * padding
            else
                maxHeight - botSize - padding + select(106, 122)
        }

        BotAnimation.update(
            botSize,
            botStart,
            botTop
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onClick(enabled = expanded) {
                expanded = false
            }
    ) {
        val shape = RoundedCornerShape(24.dp)
        Box(
            modifier = Modifier
                .alpha(alpha)
                .padding(padding.dp)
                .size(width, height)
                .let {
                    if (isDarkTheme)
                        it.shadow(8.dp, shape, ambientColor = Color.White, spotColor = Color(0xFF808080))
                    else
                        it.shadow(8.dp, shape)
                }
                .clip(shape)
                .background(MaterialTheme.colorScheme.background)
                .clickable(enabled = !expanded) {
                    if (!expanded)
                        BotAnimation.exitHover()
                    expanded = !expanded
                }
                .onPointerEvent(PointerEventType.Enter){
                    if (!expanded)
                        BotAnimation.enterHover()
                }
                .onPointerEvent(PointerEventType.Exit){
                    if (!expanded)
                        BotAnimation.exitHover()
                }
                .align(Alignment.BottomEnd)
        ) {
            var botState by remember { mutableStateOf(0) }

            LaunchedEffect(botState){
                if (botState == 1){
                    delay(1300)
                    BotAnimation.click(false)
                }
                botState = 0
            }

            if (expanded) {
                val botSize = select(116, 136).dp
                val botPadding = (padding - select(6, 9)).dp

                Box(
                    modifier = Modifier
                        .size(botSize)
                        .padding(botPadding)
                        .align(Alignment.TopCenter)
                        .onClick {
                            BotAnimation.click(true)
                            botState = 1
                        }
                        .onPointerEvent(PointerEventType.Enter){
                            BotAnimation.enterHover()
                        }
                        .onPointerEvent(PointerEventType.Exit){
                            BotAnimation.exitHover()
                        }
                )

                FilledIconButton(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(36.dp)
                        .align(Alignment.TopEnd),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = {
                        expanded = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "AI Assistant",
                    modifier = Modifier
                        .padding(top = botSize - select(10, 12).dp)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.onBackground,
                )

                var keyboardPadding by remember { mutableStateOf(0.0) }

                if (Platform.isIOS){
                    val fullHeight by remember { mutableStateOf(visualViewport.height) }
                    var currentHeight by remember { mutableStateOf(visualViewport.height) }

                    DisposableEffect(Unit) {
                        val listener = { _: Event ->
                            currentHeight = visualViewport.height
                        }

                        visualViewport.addEventListener("resize", listener)

                        onDispose {
                            visualViewport.removeEventListener("resize", listener)
                        }
                    }
                    keyboardPadding = fullHeight - currentHeight
                } else {
                    keyboardPadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding().value.toDouble()
                }

                Column(
                    modifier = Modifier
                        .padding(top = botSize + botPadding, bottom = keyboardPadding.dp)
                        .fillMaxSize()
                ){
                    if (state.loading)
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ){
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    else{
                        val listState = rememberLazyListState()

                        LaunchedEffect(state.messages.size) {
                            if (state.messages.isNotEmpty()) {
                                listState.scrollToItem(0)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .weight(1f),
                            state = listState,
                            reverseLayout = true,
                            contentPadding = PaddingValues(top = 32.dp, bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (state.waitingForResponse)
                                item(key = -1L){
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp),
                                        text = "Thinking... hang tight! Make sure not to close this page.",
                                        fontSize = select(9, 11).sp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                    BouncingDots()
                                    Spacer(
                                        modifier = Modifier
                                            .padding(top = 16.dp)
                                    )
                                }

                            if (newMessage != null){
                                item(
                                    key = newMessage!!.id
                                ) {
                                    MessageCell(
                                        newMessage!!.type,
                                        typedMessage,
                                        newMessage!!.time,
                                        width.value
                                    )
                                }
                            }

                            items(
                                state.messages,
                                key = { it.id }
                            ) { message ->
                                MessageCell(
                                    message.type,
                                    message.message,
                                    message.time,
                                    width.value
                                )
                            }

                            item(-2L) {
                                print("first message")
                                MessageCell(
                                    Message.Type.BOT,
                                    "Hi there! I'm Anas' AI assistant. Ask me anything about his experience, skills, or projects to get started.",
                                    state.messages.lastOrNull()?.time ?:
                                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time,
                                    width.value
                                )
                            }
                        }
                    }

                    Row (
                        modifier = Modifier
                            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                            .fillMaxWidth()
                            .heightIn(max = (height.value/2).dp)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceBright,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp)
                    ){
                        TextField(
                            modifier = Modifier
                                .heightIn(min = 54.dp)
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onKeyEvent { event ->
                                    if (event.key == Key.Enter && event.type == KeyEventType.KeyUp) {
                                        if (!state.waitingForResponse && state.message.isNotBlank()) {
                                            BotAnimation.think(true)
                                            viewModel.handle(Intent.Send)
                                        }
                                        true
                                    } else {
                                        false
                                    }
                                },
                            value = state.message,
                            onValueChange = {
                                viewModel.handle(Intent.UpdateMessage(it))
                            },
                            enabled = !state.waitingForResponse,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Send,
                                showKeyboardOnFocus = true
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )

                        FilledIconButton(
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            enabled = !state.waitingForResponse && state.message.isNotBlank(),
                            onClick = {
                                BotAnimation.think(true)
                                viewModel.handle(Intent.Send)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                contentDescription = null
                            )
                        }

                    }
                }

                Box(
                    modifier = Modifier
                        .padding(top = botSize + botPadding)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.background,
                                    Color.Transparent
                                )
                            )
                        )
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}