@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)

package me.anasmusa.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import me.anasmusa.portfolio.chat.Chat
import me.anasmusa.portfolio.component.AnimationOverlay
import me.anasmusa.portfolio.component.AnimationType
import me.anasmusa.portfolio.core.Language
import me.anasmusa.portfolio.core.Resource
import me.anasmusa.portfolio.core.isTablet
import me.anasmusa.portfolio.core.select
import me.anasmusa.portfolio.core.stringResource
import me.anasmusa.portfolio.main.AboutMe
import me.anasmusa.portfolio.main.Education
import me.anasmusa.portfolio.main.Experience
import me.anasmusa.portfolio.main.Footer
import me.anasmusa.portfolio.main.Projects
import me.anasmusa.portfolio.main.Toolbar
import me.anasmusa.portfolio.theme.appTypography
import me.anasmusa.portfolio.theme.darkScheme
import me.anasmusa.portfolio.theme.lightScheme
import org.jetbrains.compose.resources.ComposeEnvironment
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getSystemEnvironment
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.events.Event

val LocalWindowSize = compositionLocalOf { IntSize.Zero }

@Composable
fun WindowSizeProvider(content: @Composable () -> Unit) {
    var windowSize by remember { mutableStateOf(IntSize(window.innerWidth, window.innerHeight)) }

    LaunchedEffect(window) {
        window.onresize = {
            windowSize = IntSize(window.innerWidth, window.innerHeight)
        }
    }

    CompositionLocalProvider(LocalWindowSize provides windowSize, content)
}

@OptIn(ExperimentalResourceApi::class)
val resourceEnvironmentFix: Unit = run {
    org.jetbrains.compose.resources.getResourceEnvironment = ::myResourceEnvironment
}

private fun myResourceEnvironment(): ResourceEnvironment {
    val environment = getSystemEnvironment()
    return mapEnvironment(environment)
}


private fun mapEnvironment(
    environment: ResourceEnvironment,
    appLang: String? = null
): ResourceEnvironment {
    val qualifier = if (appLang != null)
        LanguageQualifier(appLang)
    else
        environment.language
    return ResourceEnvironment(
        language = when (qualifier.language) {
            "he" -> LanguageQualifier("iw")
            "id" -> LanguageQualifier("in")
            else -> qualifier
        },
        region = environment.region,
        theme = environment.theme,
        density = environment.density
    )
}

@Composable
fun ResourceEnvironmentFix(lang: String?, content: @Composable () -> Unit) {
    resourceEnvironmentFix
    val default = LocalComposeEnvironment.current
    CompositionLocalProvider(
        LocalComposeEnvironment provides object : ComposeEnvironment {
            @Composable
            override fun rememberEnvironment(): ResourceEnvironment {
                val environment = default.rememberEnvironment()
                return mapEnvironment(environment, lang)
            }
        },
        content
    )
}

@Composable
private fun DrawerCell(title: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(bottom = select(12, 24).dp)
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(start = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = select(13, 20).sp,
            fontWeight = FontWeight.Medium,
        )
    }
}


@Composable
fun App() {
    var isDarkTheme by remember { mutableStateOf(false) }
    var lang by remember {
        Data.lang = localStorage.getItem("lang") ?: "en"
        mutableStateOf(
            when (Data.lang) {
                Language.UZBEK.isoFormat -> Language.UZBEK
                Language.RUSSIAN.isoFormat -> Language.RUSSIAN
                else -> Language.ENGLISH
            }
        )
    }

    var stringsLoaded by remember { mutableStateOf(Resource.isLoaded) }
    LaunchedEffect(lang) {
        Resource.setLocale(lang.isoFormat) {
            stringsLoaded = true
        }
    }

    if (stringsLoaded) {
        localStorage.getItem("theme")?.let {
            if (it.toBoolean())
                isDarkTheme = true
        } ?: run {
            val query = window.matchMedia("(prefers-color-scheme: dark)")
            isDarkTheme = query.matches
            DisposableEffect(query) {
                val listener: (event: Event) -> Unit = { event ->
                    val mediaEvent = event as? MediaQueryListEvent
                    if (mediaEvent != null) {
                        isDarkTheme = mediaEvent.matches
                    }
                }
                query.addListener(listener)
                onDispose {
                    query.removeListener(listener)
                }
            }
        }

        val listState = rememberLazyListState()
        var snapshotImage by remember { mutableStateOf<ImageBitmap?>(null) }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        var insideDrawer by remember { mutableStateOf(false) }
        var drawerGesturesEnabled by remember { mutableStateOf(true) }
        var warningShowed by remember {
            mutableStateOf(
                localStorage.getItem("warning").toBoolean()
            )
        }

        MaterialTheme(
            colorScheme = if (isDarkTheme) darkScheme else lightScheme,
            typography = appTypography()
        ) {
            ResourceEnvironmentFix(lang.isoFormat) {
                WindowSizeProvider {
                    key(lang) {
                        val onMenuItemClicked: (Int) -> Unit = { position: Int ->
                            if (drawerState.isOpen)
                                coroutineScope.launch {
                                    drawerState.close()
                                }
                            coroutineScope.launch {
                                listState.animateScrollToItem(position)
                            }
                        }
                        var themeToggleSize = IntSize.Zero
                        var themeTogglePosition = Offset(0f, 0f)
                        var animationType by remember { mutableStateOf<AnimationType?>(null) }

                        val appScene = @Composable {
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                AppScene(
                                    graphicsLayer = graphicsLayer,
                                    isDarkTheme = isDarkTheme,
                                    lang = lang,
                                    listState = listState,
                                    onMainMenuStateChanged = {
                                        insideDrawer = !it
                                    },
                                    onThemeTogglePositioned = {
                                        themeToggleSize = it.size
                                        themeTogglePosition = it.positionInRoot()
                                    },
                                    onThemeChange = {
                                        if (snapshotImage != null)
                                            return@AppScene
                                        animationType = AnimationType.CIRCULAR_REVEAL(
                                            themeTogglePosition,
                                            themeToggleSize.height / 2,
                                            !isDarkTheme
                                        )
                                        coroutineScope.launch {
                                            val bitmap = graphicsLayer.toImageBitmap()
                                            isDarkTheme = !isDarkTheme
                                            localStorage.setItem("theme", isDarkTheme.toString())
                                            snapshotImage = bitmap
                                        }
                                    },
                                    onLangChange = {
                                        if (snapshotImage != null)
                                            return@AppScene
                                        animationType = AnimationType.LRT()
                                        coroutineScope.launch {
                                            val bitmap = graphicsLayer.toImageBitmap()
                                            Data.lang = it.isoFormat
                                            lang = it
                                            localStorage.setItem("lang", it.isoFormat)
                                            snapshotImage = bitmap
                                        }
                                    },
                                    onMenuClicked = {
                                        coroutineScope.launch {
                                            drawerState.open()
                                        }
                                    },
                                    onMenuItemClicked = onMenuItemClicked,
                                    onChatStateChanged = {
                                        drawerGesturesEnabled = !it
                                    }
                                )
                            }

                            AnimationOverlay(
                                snapshotImage = snapshotImage,
                                animationType,
                                onAnimationFinished = {
                                    snapshotImage = null
                                    animationType = null
                                }
                            )
                        }

                        if (insideDrawer)
                            ModalNavigationDrawer(
                                drawerState = drawerState,
                                gesturesEnabled = drawerGesturesEnabled,
                                drawerContent = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(0.7f)
                                            .background(
                                                color = MaterialTheme.colorScheme.background,
                                                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp)
                                            )
                                    ) {
                                        Spacer(modifier = Modifier.height(24.dp))

                                        DrawerCell(Strings.about_me) {
                                            onMenuItemClicked(0)
                                        }
                                        DrawerCell(Strings.experience) {
                                            onMenuItemClicked(1)
                                        }
                                        DrawerCell(Strings.education) {
                                            onMenuItemClicked(2)
                                        }
                                        DrawerCell(Strings.projects) {
                                            onMenuItemClicked(3)
                                        }
                                    }
                                }
                            ) {
                                appScene()
                            }
                        else
                            appScene()
                    }
                }

                if (!warningShowed)
                    AlertDialog(
                        onDismissRequest = {
                            warningShowed = true
                            localStorage.setItem("warning", "true")
                        },
                        title = { Text(stringResource(Strings.head_up)) },
                        text = {
                            Text(
                                text = stringResource(Strings.website_warning),
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    warningShowed = true
                                    localStorage.setItem("warning", "true")
                                },
                            ) {
                                Text(stringResource(Strings.got_it))
                            }
                        }
                    )
            }
        }
    }
}

@Composable
fun BoxWithConstraintsScope.AppScene(
    graphicsLayer: GraphicsLayer,
    isDarkTheme: Boolean,
    lang: Language,
    listState: LazyListState,
    onMainMenuStateChanged: (visible: Boolean) -> Unit,
    onThemeTogglePositioned: (LayoutCoordinates) -> Unit,
    onThemeChange: () -> Unit,
    onLangChange: (lang: Language) -> Unit,
    onMenuClicked: () -> Unit,
    onMenuItemClicked: (position: Int) -> Unit,
    onChatStateChanged: (expanded: Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .drawWithContent {
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val max = select(36, 44)
        val min = select(16, 24)

        val scrollOffset by remember {
            derivedStateOf {
                if (listState.firstVisibleItemIndex == 0) {
                    listState.firstVisibleItemScrollOffset
                } else {
                    max * 10
                }
            }
        }

        val verticalPadding = max(min.dp, min((max - (scrollOffset / 10)).dp, max.dp))

        Toolbar(
            isDarkTheme = isDarkTheme,
            lang = lang,
            padding = verticalPadding,
            onMainMenuStateChanged = onMainMenuStateChanged,
            onThemeTogglePositioned = onThemeTogglePositioned,
            onThemeChange = onThemeChange,
            onLangChange = onLangChange,
            onMenuClicked = onMenuClicked,
            onMenuItemClicked = onMenuItemClicked
        )
        val padding = if (isTablet())
            (this@AppScene.maxWidth.value * 0.125f).dp
        else
            if (LocalWindowSize.current.width > 480) 48.dp else 20.dp
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState
        ) {
            item(1) {
                AboutMe(
                    horizontalPadding = padding,
                    snackbarHostState = snackbarHostState
                )
            }
            item(2) {
                Experience(
                    horizontalPadding = padding
                )
            }
            item(3) {
                Education(
                    horizontalPadding = padding
                )
            }
            item(4) {
                Projects(
                    horizontalPadding = padding
                )
            }
            item(5) {
                Footer(
                    horizontalPadding = padding
                )
            }
        }
    }

    Chat(
        isDarkTheme = isDarkTheme,
        onStateChanged = onChatStateChanged
    )

    SnackbarHost(
        modifier = Modifier
            .align(Alignment.BottomCenter),
        hostState = snackbarHostState,
        snackbar = {
            Snackbar(
                snackbarData = it,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}