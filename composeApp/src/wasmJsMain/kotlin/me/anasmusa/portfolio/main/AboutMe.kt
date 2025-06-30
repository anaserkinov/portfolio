package me.anasmusa.portfolio.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import me.anasmusa.portfolio.Data
import me.anasmusa.portfolio.Platform
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.core.select
import me.anasmusa.portfolio.core.stringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.imageResource
import org.w3c.dom.HTMLAnchorElement
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_github
import portfolio.composeapp.generated.resources.ic_linkedin
import portfolio.composeapp.generated.resources.ic_mail
import portfolio.composeapp.generated.resources.ic_resume
import portfolio.composeapp.generated.resources.ic_telegram
import portfolio.composeapp.generated.resources.me

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutMe(
    horizontalPadding: Dp,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(
                start = horizontalPadding,
                top = select(80, 200).dp,
                end = horizontalPadding,
                bottom = select(150, 250).dp
            )
    ) {
        Image(
            modifier = Modifier
                .size(select(53, 89).dp)
                .clip(CircleShape),
            bitmap = imageResource(Res.drawable.me),
            contentDescription = null
        )
        Text(
            text = stringResource(Strings.welcome_text),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = select(25, 52).sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .padding(top = select(15, 10).dp),
            text = stringResource(Strings.about_text),
            color = MaterialTheme.colorScheme.secondary,
            fontSize = select(10, 24).sp,
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(select(12, 28).dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val height = select(22, 44).dp
            val size = select(8, 19).sp
            Chip(
                height = height,
                size = size,
                text = "LinkedIn",
                icon = Res.drawable.ic_linkedin,
                onClick = {
                    window.open(Data.linkedin, "_blank")
                }
            )
            Chip(
                height = height,
                size = size,
                text = "GitHub",
                icon = Res.drawable.ic_github,
                onClick = {
                    window.open(Data.github, "_blank")
                }
            )
            if (Platform.isMobile)
                Chip(
                    height = height,
                    size = size,
                    text = Data.email,
                    icon = Res.drawable.ic_mail,
                    onClick = {
                        window.open("mailto:${Data.email}?body=Hi%20Anas,", "_parent")
                    },
                    onLongClick = {
                        clipboardManager.setText(AnnotatedString.Builder(Data.email).toAnnotatedString())
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(stringResource(Strings.copy_message))
                        }
                    }
                )
            else
                Chip(
                    height = height,
                    size = size,
                    text = Data.email,
                    icon = Res.drawable.ic_mail,
                    selectable = true,
                    onClick = {
                        window.open("mailto:${Data.email}?body=Hi%20Anas,", "_parent")
                    }
                )
            Chip(
                height = height,
                size = size,
                text = "Telegram",
                icon = Res.drawable.ic_telegram,
                onClick = {
                    window.open(Data.telegram, "_blank")
                }
            )
            Chip(
                height = height,
                size =  size,
                text = "CV",
                icon = Res.drawable.ic_resume,
                onClick = {
                    val link = document.createElement("a") as HTMLAnchorElement
                    link.href = "Anas_resume.pdf"
                    link.download = "Anas_resume.pdf"
                    link.click()
                }
            )
        }
    }
}