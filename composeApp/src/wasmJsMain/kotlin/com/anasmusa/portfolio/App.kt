@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)

package com.anasmusa.portfolio

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.anasmusa.portfolio.component.AnimationOverlay
import com.anasmusa.portfolio.component.AnimationType
import com.anasmusa.portfolio.main.AboutMe
import com.anasmusa.portfolio.main.Education
import com.anasmusa.portfolio.main.Experience
import com.anasmusa.portfolio.main.Projects
import com.anasmusa.portfolio.main.Toolbar
import com.anasmusa.portfolio.theme.appTypography
import com.anasmusa.portfolio.theme.darkScheme
import com.anasmusa.portfolio.theme.lightScheme
import com.anasmusa.portfolio.utils.isTablet
import com.anasmusa.portfolio.utils.select
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ComposeEnvironment
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.LocalComposeEnvironment
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getSystemEnvironment
import org.jetbrains.compose.resources.stringResource
import org.w3c.dom.MediaQueryListEvent
import org.w3c.dom.events.Event
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.about_me
import portfolio.composeapp.generated.resources.education
import portfolio.composeapp.generated.resources.experience
import portfolio.composeapp.generated.resources.projects
import kotlin.math.hypot

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
private fun DrawerCell(title: StringResource, onClick: () -> Unit) {
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
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ){
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
                                        themeToggleSize.height/2,
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
                                onMenuItemClicked = onMenuItemClicked
                            )
                        }

                        themeToggleSize.height/2
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

                                    DrawerCell(Res.string.about_me) {
                                        onMenuItemClicked(0)
                                    }
                                    DrawerCell(Res.string.experience) {
                                        onMenuItemClicked(1)
                                    }
                                    DrawerCell(Res.string.education) {
                                        onMenuItemClicked(2)
                                    }
                                    DrawerCell(Res.string.projects) {
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
        }
    }
}

@Composable
fun BoxScope.AppScene(
    graphicsLayer: GraphicsLayer,
    isDarkTheme: Boolean,
    lang: Language,
    listState: LazyListState,
    onMainMenuStateChanged: (visible: Boolean) -> Unit,
    onThemeTogglePositioned: (LayoutCoordinates) -> Unit,
    onThemeChange: () -> Unit,
    onLangChange: (lang: Language) -> Unit,
    onMenuClicked: () -> Unit,
    onMenuItemClicked: (position: Int) -> Unit
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
        LazyColumn(
            modifier = Modifier.let {
                if (isTablet())
                    it.fillMaxWidth(0.75f)
                else
                    it.fillMaxSize()
                        .padding(horizontal = if (LocalWindowSize.current.width > 480) 48.dp else 20.dp)
            }.align(Alignment.CenterHorizontally),
            state = listState
        ) {
            item(1) {
                AboutMe(snackbarHostState)
            }
            item(2) {
                Experience()
            }
            item(3) {
                Education()
            }
            item(4) {
                Projects()
            }
        }
    }

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