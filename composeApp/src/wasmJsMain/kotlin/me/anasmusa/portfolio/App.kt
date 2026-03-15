@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)

package me.anasmusa.portfolio

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.shimmerSpec
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import me.anasmusa.portfolio.chat.Chat
import me.anasmusa.portfolio.component.AnimationOverlay
import me.anasmusa.portfolio.component.AnimationType
import me.anasmusa.portfolio.core.*
import me.anasmusa.portfolio.core.Resource
import me.anasmusa.portfolio.main.*
import me.anasmusa.portfolio.theme.appTypography
import me.anasmusa.portfolio.theme.darkScheme
import me.anasmusa.portfolio.theme.lightScheme
import org.jetbrains.compose.resources.*
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
            .padding(bottom = deviceValue(12, 24).dp)
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick)
            .padding(start = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = deviceValue(13, 20).sp,
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
                            val shimmerTheme = remember {
                                defaultShimmerTheme.copy(
                                    animationSpec = infiniteRepeatable(
                                        animation = shimmerSpec(
                                            durationMillis = 1000,
                                            easing = LinearEasing,
                                            delayMillis = 600,
                                        ),
                                        repeatMode = RepeatMode.Restart,
                                    )
                                )
                            }
                            CompositionLocalProvider(
                                LocalShimmerTheme provides shimmerTheme
                            ) {
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
    val viewModel = viewModel { MainViewModel() }
    val state by viewModel.state.collectAsState()
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
        val max = deviceValue(36, 44)
        val min = deviceValue(16, 24)

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
                AboutSection(
                    modifier = Modifier.padding(horizontal = padding),
                    data = state.about,
                    snackbarHostState = snackbarHostState
                )
                LaunchedEffect(Unit) {
                    viewModel.handle(MainIntent.LoadAbout)
                }
            }
            item(2) {
                ExperienceSection(
                    modifier = Modifier.padding(horizontal = padding),
                    data = state.experience,
                )
                LaunchedEffect(Unit) {
                    viewModel.handle(MainIntent.LoadExperience)
                }
            }
            item(3) {
                EducationSection(
                    modifier = Modifier.padding(horizontal = padding),
                    data = state.education,
                )
                LaunchedEffect(Unit) {
                    viewModel.handle(MainIntent.LoadEducation)
                }
            }
//            item(4) {
//                LanguageSection(
//                    modifier = Modifier.padding(horizontal = padding),
//                    data = state.language,
//                )
//                LaunchedEffect(Unit) {
//                    viewModel.handle(MainIntent.LoadLanguage)
//                }
//            }
//            item(5) {
//                SkillsSection(
//                    modifier = Modifier.padding(horizontal = padding),
//                    data = state.skills,
//                )
//                LaunchedEffect(Unit) {
//                    viewModel.handle(MainIntent.LoadSkill)
//                }
//            }
//
//            item(6) {
//                ProjectSection(
//                    modifier = Modifier.padding(horizontal = padding),
//                    data = state.projects,
//                    isAllLoading = state.isAllProjectsLoading,
//                    loadMore = { viewModel.handle(MainIntent.LoadProjects(true)) },
//                )
//                LaunchedEffect(Unit) {
//                    viewModel.handle(MainIntent.LoadProjects(false))
//                }
//            }
            item(7) {
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