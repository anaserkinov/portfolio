package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.anasmusa.portfolio.core.deviceValue
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.figma_credit
import portfolio.composeapp.generated.resources.rive_credit

@Composable
private fun Item(
    modifier: Modifier,
    text: StringResource,
    name: String,
    link: String
){
    val colorPrimary = MaterialTheme.colorScheme.primary
    val fullText = stringResource(text, name)
    val text = remember(colorPrimary, fullText) {
        buildAnnotatedString {
            if (fullText.isEmpty()) return@buildAnnotatedString
            val startIndex = fullText.indexOf(name)
            val endIndex = startIndex + name.length

            append(fullText)

            addStyle(
                style = SpanStyle(
                    color = colorPrimary,
                    fontWeight = FontWeight.Bold
                ),
                start = startIndex,
                end = endIndex
            )

            addLink(
                LinkAnnotation.Url(
                    url = link,
                    styles = TextLinkStyles()
                ),
                start = startIndex,
                end = endIndex
            )
        }
    }
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = deviceValue(10, 14).sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun Footer(
    horizontalPadding: Dp
){
    Column(
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ){
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceBright
        )
        Item(
            modifier = Modifier
                .padding(top = 24.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = horizontalPadding),
            text = Res.string.figma_credit,
            name = "José Luis",
            link = "https://www.figma.com/community/file/1357544466051780951/personal-portfolio"
        )
        Item(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = horizontalPadding),
            text = Res.string.rive_credit,
            name = "GowthamSelvaraj",
            link = "https://rive.app/marketplace/20336-38229-emotional-chatbot-animation-built-with-boolean-magic/"
        )
    }
}