package me.anasmusa.portfolio.main

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.browser.window
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.System
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.TextWithHeight
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.*
import org.jetbrains.compose.resources.painterResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_android
import portfolio.composeapp.generated.resources.ic_chevron_down
import portfolio.composeapp.generated.resources.ic_code

@Composable
fun ProjectSection(
    modifier: Modifier,
    data: ProjectResponse?,
    isAllLoading: Boolean,
    loadMore: () -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        item {
            Title(
                Res.drawable.ic_code,
                Strings.projects
            )

            Spacer(Modifier.height(deviceValue(6, 12).dp))
        }

        data?.let { data ->
            items(data.entities) {
                Cell(it)
            }

            if (isAllLoading){

            } else if (data.entities.size < data.totalCount) {
                item {
                    val height = deviceValue(18, 36).dp
                    val size = deviceValue(8, 16).sp
                    Chip(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        height = height,
                        size = size,
                        text = "Show more",
                        icon = Res.drawable.ic_chevron_down,
                        onClick = loadMore
                    )
                }
            }
        } ?: run {

        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Cell(project: ProjectResponse.Entity) {
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
            AsyncImage(
                model = project.logoUrl.withDownloadBaseUrl(),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Text(
                modifier = Modifier
                    .padding(end = 6.dp),
                text = project.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = titleSize,
                fontWeight = FontWeight.SemiBold
            )

            val tooltipPositionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                positioning = TooltipAnchorPosition.Above
            )

            project.systems.forEach { system ->
                val (icon, name, isTintable) = when (system) {
                    System.Android -> {
                        Triple(Res.drawable.ic_android, Strings.android_app, false)
                    }
                }

                BasicTooltipBox(
                    positionProvider = tooltipPositionProvider,
                    state = rememberBasicTooltipState(),
                    content = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .size(20.dp),
                            painter = painterResource(icon),
                            tint = if (isTintable)
                                MaterialTheme.colorScheme.onBackground
                            else
                                Color.Unspecified,
                            contentDescription = null
                        )
                    },
                    tooltip = {
                        TextWithHeight(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .widthIn(max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            text = stringResource(name),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = descriptionSize
                        )
                    }
                )
            }

            if (project.isWhiteLabel) {
                BasicTooltipBox(
                    positionProvider = tooltipPositionProvider,
                    state = rememberBasicTooltipState(),
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
                        TextWithHeight(
                            modifier = Modifier
                                .widthIn(max = 300.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            text = stringResource(Strings.white_label_tip),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = descriptionSize
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = project.date,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = descriptionSize,
                lineHeight = descriptionSize
            )
        }
        Text(
            modifier = Modifier,
            text = project.description.short,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = descriptionSize
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        ) {
            project.description.items.forEach { item ->
                Spacer(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
                ProjectInfo(item.value, itemSize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val height = deviceValue(18, 36).dp
        val size = deviceValue(8, 16).sp
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            project.links.forEach {
                val (text, icon) = it.textAndIcon()
                Chip(
                    height = height,
                    size = size,
                    text = text,
                    icon = icon,
                    onClick = {
                        window.open(it.value, "_blank")
                    }
                )
            }
        }

        Spacer(Modifier.height(56.dp))
    }
}

@Composable
private fun ProjectInfo(text: String, fontSize: TextUnit) {
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
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium
        )

    }
}
