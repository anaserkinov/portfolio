package com.anasmusa.portfolio.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anasmusa.portfolio.Language
import com.anasmusa.portfolio.component.TextWithHeight
import com.anasmusa.portfolio.utils.isTablet
import com.anasmusa.portfolio.utils.select
import kotlinx.coroutines.launch
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.about_me
import portfolio.composeapp.generated.resources.education
import portfolio.composeapp.generated.resources.experience
import portfolio.composeapp.generated.resources.ic_hamburger
import portfolio.composeapp.generated.resources.ic_moon
import portfolio.composeapp.generated.resources.ic_sun
import portfolio.composeapp.generated.resources.projects

@Composable
private fun Title(
    title: StringResource,
    onClick: () -> Unit
) {
    TextWithHeight(
        modifier = Modifier
            .padding(end = select(14, 34).dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        text = stringResource(title),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = select(13, 20).sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun LanguageSelector(
    selectedLanguage: Language,
    size: Dp,
    onLanguageSelected: (Language) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val padding = select(2, 4).dp

    Box(
        modifier = Modifier
    ) {
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(size),
                painter = painterResource(selectedLanguage.icon),
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.entries.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(size)
                                    .padding(padding),
                                painter = painterResource(language.icon),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(language.title)
                        }
                    },
                    onClick = {
                        onLanguageSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun Toolbar(
    content: @Composable () -> Unit,
    menu: @Composable () -> Unit,
    onMainMenuStateChanged: (visible: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val isTablet = isTablet()
    SubcomposeLayout (
        modifier = Modifier
            .fillMaxWidth()
    ){ constraints ->

        val mainPlaceables = subcompose("measure", content).map {
            it.measure(Constraints())
        }
        
        val totalWidth = mainPlaceables.sumOf { it.width }

        val showMain = totalWidth <= constraints.maxWidth || isTablet

        onMainMenuStateChanged(showMain)

        val finalPlaceables = if (showMain) {
            subcompose("main", content).map {
                it.measure(constraints)
            }
        } else {
            subcompose("menu", menu).map {
                it.measure(constraints)
            }
        }

        layout(
            width = constraints.maxWidth,
            height = finalPlaceables.maxOfOrNull { it.height } ?: 0) {
            var xPos = 0
            finalPlaceables.forEach {
                it.placeRelative(x = xPos, y = 0)
                xPos += it.width
            }
        }
    }
}

@Composable
private fun LangTheme(
    isDarkTheme: Boolean,
    lang: Language,
    onThemeTogglePositioned: (LayoutCoordinates) -> Unit,
    onThemeChange: () -> Unit,
    onLangChange: (lang: Language) -> Unit
){
    IconButton(
        modifier = Modifier
            .onGloballyPositioned(onThemeTogglePositioned),
        onClick = onThemeChange
    ) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(select(24, 36).dp),
            painter = painterResource(if (isDarkTheme) Res.drawable.ic_sun else Res.drawable.ic_moon),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null
        )
    }

    Spacer(modifier = Modifier.padding(end = 8.dp))

    LanguageSelector(
        lang,
        select(24, 36).dp,
        onLanguageSelected = onLangChange
    )
}

@Composable
fun Toolbar(
    isDarkTheme: Boolean,
    lang: Language,
    padding: Dp,
    onMainMenuStateChanged: (visible: Boolean) -> Unit,
    onThemeTogglePositioned: (LayoutCoordinates) -> Unit,
    onThemeChange: () -> Unit,
    onLangChange: (lang: Language) -> Unit,
    onMenuClicked: () -> Unit,
    onMenuItemClicked: (position: Int) -> Unit
) {
    Toolbar(
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = padding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.width(select(12, 24).dp))

                Title(
                    title = Res.string.about_me,
                    onClick = {
                        onMenuItemClicked(0)
                    }
                )
                Title(
                    title = Res.string.experience,
                    onClick = {
                        onMenuItemClicked(1)
                    }
                )
                Title(
                    title = Res.string.education,
                    onClick = {
                        onMenuItemClicked(2)
                    }
                )
                Title(
                    title = Res.string.projects,
                    onClick = {
                        onMenuItemClicked(3)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                LangTheme(isDarkTheme, lang, onThemeTogglePositioned, onThemeChange, onLangChange)

                Spacer(modifier = Modifier.width(select(12, 24).dp))
            }
        },
        menu = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = padding),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Spacer(modifier = Modifier.width(select(12, 24).dp))

                IconButton(
                    modifier = Modifier,
                    onClick = onMenuClicked
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(select(24, 36).dp),
                        painter = painterResource(Res.drawable.ic_hamburger),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LangTheme(isDarkTheme, lang, onThemeTogglePositioned, onThemeChange, onLangChange)

                Spacer(modifier = Modifier.width(select(12, 24).dp))
            }
        },
        onMainMenuStateChanged = onMainMenuStateChanged
    )
}