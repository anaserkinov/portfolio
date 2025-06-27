package me.anasmusa.portfolio.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.anasmusa.portfolio.Data
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.InfoCell
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.select
import kotlinx.datetime.format
import me.anasmusa.portfolio.Strings
import org.jetbrains.compose.resources.ExperimentalResourceApi
import me.anasmusa.portfolio.core.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.allStringResources
import portfolio.composeapp.generated.resources.ic_edu
import portfolio.composeapp.generated.resources.ic_hash
import portfolio.composeapp.generated.resources.ic_lang

@Composable
private fun LanguageCell(
    name: Int,
    level: Int,
    percentage: Int
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = buildAnnotatedString {
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                append(stringResource(name))
                append(" - ")
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
                append(stringResource(level))
            },
            fontSize = select(13, 22).sp,
            fontWeight = FontWeight.Medium
        )

        val colorPrimary = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
        ){
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillCell(item: Data.Skill, bottomSpace: Dp){
    if (item.title != null)
        Text(
            modifier = Modifier
                .padding(bottom = select(3, 6).dp),
            text = stringResource(item.title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = select(13, 22).sp,
            fontWeight = FontWeight.Medium
        )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(11.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ){
        val height = select(21, 30).dp
        val size = select(10, 15).sp
        item.items.forEach {
            Chip(
                height = height,
                size = size,
                text = it.title
            )
        }
    }
    if (bottomSpace > 0.dp)
        Spacer(Modifier.height(bottomSpace))
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Education(
    horizontalPadding: Dp
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = select(150, 220).dp
            )
    ) {

        Title(
            Res.drawable.ic_edu,
            Strings.education
        )

        Data.education.forEach {
            InfoCell(
                it.startDate.format(Data.dateFormatter) + " - " + if (it.endDate != null)
                    it.endDate.format(Data.dateFormatter)
                else
                    stringResource(Strings.present),
                stringResource(it.university) + " - " + stringResource(it.field),
                it.items
            )
        }

        Spacer(Modifier.height(60.dp))

        Title(
            Res.drawable.ic_lang,
            Strings.languages
        )
        Data.languages.forEachIndexed { index, language ->
            LanguageCell(
                language.name,
                language.levelName,
                when(language.level){
                    "a1" -> 10
                    "a2" -> 27
                    "b1" -> 44
                    "b2" -> 61
                    "c1" -> 78
                    "c2" -> 95
                    else -> 100
                }
            )
            if (index != Data.languages.size - 1)
                Spacer(Modifier.height(select(20, 28).dp))
        }

        Spacer(Modifier.height(64.dp))
        Title(
            Res.drawable.ic_hash,
            Strings.skills
        )

        Data.skills.forEachIndexed { index, item ->
            SkillCell(
                item,
                if (Data.skills.getOrNull(index + 1)?.title != null)
                    select(12, 20).dp
                else
                    4.dp
            )
        }
    }
}