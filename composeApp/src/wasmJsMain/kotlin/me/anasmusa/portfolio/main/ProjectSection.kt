package me.anasmusa.portfolio.main

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.browser.window
import me.anasmusa.portfolio.Strings
import me.anasmusa.portfolio.api.model.ProjectResponse
import me.anasmusa.portfolio.api.model.System
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.ShimmerBox
import me.anasmusa.portfolio.component.TextWithHeight
import me.anasmusa.portfolio.component.Title
import me.anasmusa.portfolio.core.*
import org.jetbrains.compose.resources.painterResource
import portfolio.composeapp.generated.resources.*

fun LazyListScope.projectSection(
    modifier: Modifier,
    shimmer: Shimmer,
    data: ProjectResponse?,
    isAllLoading: Boolean,
    loadProjects: (isAll: Boolean) -> Unit
) {

    item {
        Title(
            modifier = modifier,
            icon = Res.drawable.ic_code,
            title = Strings.projects
        )

        Spacer(Modifier.height(deviceValue(6, 12).dp))

        LaunchedEffect(Unit){
            loadProjects(false)
        }
    }

    data?.let { data ->
        items(data.entities, contentType = { 1000 }) {
            Cell(
                modifier = modifier,
                project = it
            )
        }

        if (isAllLoading){
            items(3){
                ShimmerCell(
                    modifier = modifier
                        .shimmer(shimmer),
                )
            }
        } else if (data.entities.size < data.totalCount) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    val height = deviceValue(18, 36).dp
                    val size = deviceValue(8, 16).sp
                    Chip(
                        modifier = Modifier
                            .padding(bottom = 20.dp),
                        height = height,
                        size = size,
                        text = "Show more",
                        icon = Res.drawable.ic_chevron_down,
                        onClick = {
                            loadProjects(true)
                        }
                    )
                }
            }
        }
    } ?: run {
        items(3){
            ShimmerCell(
                modifier = modifier
                    .shimmer(shimmer),
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun Cell(
    modifier: Modifier = Modifier,
    project: ProjectResponse.Entity
) {
    Column(
        modifier = modifier.fillMaxWidth()
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
                model = project.logoPath.withDownloadBaseUrl(),
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
                        Triple(Res.drawable.ic_android, Strings.android, false)
                    }
                    System.ComposeMultiplatform -> {
                        Triple(Res.drawable.ic_compose_multiplatform, Strings.compose_multiplatform, false)
                    }
                    System.KtorServer -> {
                        Triple(Res.drawable.ic_ktor, Strings.ktor_server, false)
                    }
                    System.ReactJs -> {
                        Triple(Res.drawable.ic_react_js, Strings.react_js, false)
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

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val height = deviceValue(18, 36).dp
            val size = deviceValue(8, 16).sp
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ShimmerCell(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(56.dp)
            )
            ShimmerBox(
                modifier = Modifier
                    .height(deviceValue(24, 32).dp)
                    .width(deviceValue(100, 200).dp)
                    .padding(end = 6.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            ShimmerBox(
                modifier = Modifier
                    .height(deviceValue(16, 24).dp)
                    .width(deviceValue(120, 220).dp)
                    .padding(end = 6.dp)
            )
        }

        ShimmerBox(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .height(deviceValue(16, 24).dp)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp)
        ) {
            repeat(4){
                Spacer(modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
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

                    ShimmerBox(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .fillMaxWidth()
                            .height(deviceValue(16, 24).dp)
                    )

                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val height = deviceValue(18, 36).dp
            val width = deviceValue(70, 140).dp
            repeat(2) {
                ShimmerBox(
                    modifier = Modifier
                        .height(height)
                        .width(width),
                    shape = RoundedCornerShape(height/2)
                )
            }
        }

        Spacer(Modifier.height(56.dp))
    }
}
