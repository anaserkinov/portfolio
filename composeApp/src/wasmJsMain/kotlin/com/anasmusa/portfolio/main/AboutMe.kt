package com.anasmusa.portfolio.main

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anasmusa.portfolio.Data
import com.anasmusa.portfolio.component.Chip
import com.anasmusa.portfolio.utils.select
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.about_text
import portfolio.composeapp.generated.resources.copy_message
import portfolio.composeapp.generated.resources.ic_github
import portfolio.composeapp.generated.resources.ic_linkedin
import portfolio.composeapp.generated.resources.ic_mail
import portfolio.composeapp.generated.resources.ic_resume
import portfolio.composeapp.generated.resources.ic_telegram
import portfolio.composeapp.generated.resources.me
import portfolio.composeapp.generated.resources.welcome_text

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutMe(
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(
                top = select(80, 200).dp,
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
            text = stringResource(Res.string.welcome_text),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = select(25, 52).sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .padding(top = select(15, 10).dp),
            text = stringResource(Res.string.about_text),
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
            Chip(
                height = height,
                size = size,
                text = Data.email,
                icon = Res.drawable.ic_mail,
                copyable = true,
                onClick = {
                    window.open("mailto:${Data.email}?body=Hi%20Anas,", "_parent")
                },
                onLongClick = {
                    clipboardManager.setText(AnnotatedString.Builder(Data.email).toAnnotatedString())
                    coroutineScope.launch{
                        snackbarHostState.showSnackbar(getString(Res.string.copy_message))
                    }
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
                icon = Res.drawable.ic_resume
            )
        }
    }
}