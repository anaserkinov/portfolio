package me.anasmusa.portfolio.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.valentinilk.shimmer.shimmer
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.api.model.LanguageResponse
import me.anasmusa.portfolio.component.ShimmerBox
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.deviceValue
import me.anasmusa.portfolio.core.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_lang


@OptIn(ExperimentalResourceApi::class)
@Composable
fun LanguageSection(
    modifier: Modifier,
    data: LanguageResponse?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 64.dp),
        verticalArrangement = Arrangement.spacedBy(deviceValue(20, 28).dp)
    ) {
        Title(
            icon = Res.drawable.ic_lang,
            title = Strings.languages
        )
        data?.let { data ->
            data.entities.forEach { language ->
                LanguageCell(
                    language.name,
                    language.level
                )
            }
        } ?: run {
            Column(modifier = Modifier.shimmer()) {
                repeat(2) {
                    ShimmerBox(
                        modifier = Modifier
                            .fillMaxWidth(0.25f)
                            .height(deviceValue(16, 24).dp)
                    )
                    ShimmerBox(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth(0.75f)
                            .height(deviceValue(8, 16).dp)
                    )
                    Spacer(
                        modifier = Modifier.height(
                            deviceValue(20, 28).dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguageCell(
    name: String,
    level: String,
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                append(name)
                append(" - ")
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
                append(
                    stringResource(
                        when(level){
                            "a1" -> Strings.a1
                            "a2" -> Strings.a2
                            "b1" -> Strings.b1
                            "b2" -> Strings.b2
                            "c1" -> Strings.c1
                            "c2" -> Strings.c2
                            else -> Strings.native
                        }
                    )
                )
            },
            fontSize = deviceValue(13, 22).sp,
            fontWeight = FontWeight.Medium
        )

        val colorPrimary = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier.fillMaxWidth()
                .padding(top = deviceValue(8, 16).dp)
        ){
            val percentage = when(level){
                "a1" -> 10
                "a2" -> 27
                "b1" -> 44
                "b2" -> 61
                "c1" -> 78
                "c2" -> 95
                else -> 100
            }
            drawLine(
                color = colorPrimary,
                start = Offset(0f, 0f),
                end = Offset(size.width * percentage / 100, 0f),
                strokeWidth = 6.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}