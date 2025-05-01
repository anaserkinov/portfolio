package com.anasmusa.portfolio.main

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformParagraphStyle
import androidx.compose.ui.text.PlatformSpanStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextDecorationLineStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anasmusa.portfolio.Data
import com.anasmusa.portfolio.component.Chip
import com.anasmusa.portfolio.component.TextWithHeight
import com.anasmusa.portfolio.component.Title
import com.anasmusa.portfolio.utils.isTablet
import com.anasmusa.portfolio.utils.select
import kotlinx.browser.window
import kotlinx.datetime.format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.skia.FontFeature
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_chevron_down
import portfolio.composeapp.generated.resources.ic_code
import portfolio.composeapp.generated.resources.present
import portfolio.composeapp.generated.resources.projects
import portfolio.composeapp.generated.resources.white_label_tip

@OptIn(
    ExperimentalResourceApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun Cell(project: Data.Project) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val titleSize: TextUnit
        val descriptionSize: TextUnit
        val itemSize: TextUnit

        if (isTablet()) {
            titleSize = 22.sp
            descriptionSize = 14.sp
            itemSize = 17.sp
        } else {
            titleSize = 13.sp
            descriptionSize = 9.sp
            itemSize = 11.sp
        }

        Row(
            modifier = Modifier
                .padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painterResource(project.logo),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(end = 6.dp),
                text = project.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = titleSize,
                fontWeight = FontWeight.SemiBold
            )
            project.systems.forEach {
                Icon(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(20.dp),
                    painter = painterResource(it.icon),
                    tint = if (it.type == "android")
                        Color.Unspecified
                    else
                        MaterialTheme.colorScheme.onBackground,
                    contentDescription = null
                )
            }

            if (project.isWhiteLabel) {
                val tooltipPositionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider()
                val tooltipState = rememberBasicTooltipState()

                BasicTooltipBox(
                    positionProvider = tooltipPositionProvider,
                    state = tooltipState,
                    content = {
                        TextWithHeight(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(6.dp),
                            text = "White-Label",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = descriptionSize
                        )
                    },
                    tooltip = {
                        Text(
                            modifier = Modifier
                                .widthIn(max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            text = stringResource(Res.string.white_label_tip),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                project.startDate.format(Data.dateFormatter) + " - " + if (project.endDate != null)
                    project.endDate.format(Data.dateFormatter)
                else
                    stringResource(Res.string.present),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = descriptionSize,
                lineHeight = descriptionSize
            )
        }
        Text(
            modifier = Modifier,
            text = stringResource(project.description),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = descriptionSize
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        ) {
            project.subDescription.forEachIndexed { index, item ->
                Spacer(
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                ProjectInfo(item, itemSize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val height = select(18, 36).dp
        val size = select(8, 16).sp
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            project.links.forEach {
                Chip(
                    height = height,
                    size = size,
                    text = it.title,
                    icon = it.icon,
                    onClick = {
                        window.open(it.link, "_blank")
                    }
                )
            }
        }

        Spacer(Modifier.height(56.dp))
    }
}

@Composable
private fun ProjectInfo(text: StringResource, fontSize: TextUnit) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(6.dp)
                .background(MaterialTheme.colorScheme.onBackground, CircleShape)
                .padding(16.dp)
        )

        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = stringResource(text),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium
        )

    }
}

@Composable
fun Projects() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Title(
            Res.drawable.ic_code,
            Res.string.projects
        )

        Spacer(Modifier.height(select(6, 12).dp))

        Data.projects.forEach {
            Cell(it)
        }

        if (Data.otherProjects.isNotEmpty()){
            var showMore by remember { mutableStateOf(false) }
            if (showMore){
                Data.otherProjects.forEach {
                    Cell(it)
                }
            } else {
                val height = select(18, 36).dp
                val size = select(8, 16).sp
                Chip(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 20.dp),
                    height = height,
                    size = size,
                    text = "Show more",
                    icon = Res.drawable.ic_chevron_down,
                    onClick = {
                        showMore = true
                    }
                )
            }
        }

    }

}