package me.anasmusa.portfolio.main

import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberBasicTooltipState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.browser.window
import me.anasmusa.portfolio.api.model.Platform
import me.anasmusa.portfolio.component.*
import me.anasmusa.portfolio.core.*
import me.anasmusa.portfolio.data.model.Project
import me.anasmusa.portfolio.data.model.Project.PlatformInfo
import org.jetbrains.compose.resources.stringResource
import portfolio.composeapp.generated.resources.Res
import portfolio.composeapp.generated.resources.ic_chevron_down
import portfolio.composeapp.generated.resources.ic_code
import portfolio.composeapp.generated.resources.projects
import portfolio.composeapp.generated.resources.white_label_tip
import portfolio.composeapp.generated.resources.all

fun LazyListScope.projectSection(
    modifier: Modifier,
    shimmer: Shimmer,
    projects: List<Project>?,
    totalProjectsCount: Int,
    platforms: List<PlatformInfo>,
    selectedPlatform: Platform?,
    isAllLoading: Boolean,
    loadProjects: (isAll: Boolean) -> Unit,
    selectPlatform: (platform: Platform?) -> Unit,
) {

    item {
        Title(
            modifier = modifier,
            icon = Res.drawable.ic_code,
            title = Res.string.projects
        )

        FlowRow(
            modifier = modifier.fillMaxWidth()
                .let {
                    if (platforms.isEmpty()) it.shimmer(shimmer)
                    else it
                },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val height = deviceValue(18, 36).dp
            if (platforms.isNotEmpty()){
                val size = deviceValue(8, 16).sp
                FilterChip(
                    selected = selectedPlatform == null,
                    size = size,
                    text = stringResource(Res.string.all) + "(${totalProjectsCount})",
                    onClick = {
                        selectPlatform(null)
                    }
                )
                platforms.forEach {
                    FilterChip(
                        selected = selectedPlatform == it.platform,
                        size = size,
                        text = it.platform.name + "(${it.count})",
                        onClick = {
                            selectPlatform(it.platform)
                        }
                    )
                }
            } else {
                val width = deviceValue(70, 140).dp
                repeat(5) {
                    ShimmerBox(
                        modifier = Modifier
                            .height(height)
                            .width(width),
                        shape = RoundedCornerShape(height/2)
                    )
                }
            }
        }

        Spacer(Modifier.height(deviceValue(20, 28).dp))

        LaunchedEffect(Unit){
            loadProjects(false)
        }
    }

    projects?.let { projects ->
        items(projects, contentType = { 1000 }) {
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
        } else if (projects.size < totalProjectsCount && selectedPlatform == null) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    val height = deviceValue(24, 36).dp
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
    project: Project
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val titleSize: TextUnit
        val descriptionSize: TextUnit
        val itemSize: TextUnit

        if (isTablet()) {
            titleSize = 22.sp
            descriptionSize = 16.sp
            itemSize = 17.sp
        } else {
            titleSize = 13.sp
            descriptionSize = 10.sp
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

            if (project.isWhiteLabel) {
                BasicTooltipBox(
                    positionProvider = tooltipPositionProvider,
                    state = rememberBasicTooltipState(),
                    content = {
                        TextWithHeight(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(vertical = 6.dp, horizontal = 12.dp),
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
                            text = stringResource(Res.string.white_label_tip),
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
                ProjectInfo(item, itemSize)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (project.techStack != null){
            var expanded by remember { mutableStateOf(false) }
            var isOverflowing by remember { mutableStateOf(false) }

            Text(
                text = project.techStack,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                fontStyle = FontStyle.Italic,
                onTextLayout = { result ->
                    if (!expanded) {
                        isOverflowing = result.hasVisualOverflow
                    }
                },
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (isOverflowing || expanded) {
                        expanded = !expanded
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val height = deviceValue(24, 36).dp
            val size = deviceValue(12, 16).sp
            project.links.forEach {
                val (text, icon) = it.textAndIcon()
                Chip(
                    height = height,
                    size = size,
                    text = if (it.label != null)
                        "$text(${it.label})"
                    else
                        text,
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
