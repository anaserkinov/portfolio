package me.anasmusa.portfolio.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import me.anasmusa.portfolio.core.deviceValue
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RootChip(
    modifier: Modifier = Modifier,
    height: Dp,
    size: TextUnit,
    text: String,
    filled: Boolean = true,
    icon: DrawableResource? = null,
    onClick: () -> Unit
){
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                modifier = Modifier
                    .wrapContentHeight(),
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = size,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = size
            )
        },
        modifier = modifier
            .height(height),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (filled) MaterialTheme.colorScheme.surface
            else Color.Unspecified
        ),
        leadingIcon = {
            if (icon != null)
                Icon(
                    modifier = Modifier
                        .height(deviceValue(16, 32).dp),
                    painter = painterResource(icon),
                    tint = MaterialTheme.colorScheme.onSurface,
                    contentDescription = null
                )
        },
        shape = RoundedCornerShape(height / 2)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Chip(
    modifier: Modifier = Modifier,
    height: Dp,
    size: TextUnit,
    text: String,
    filled: Boolean = true,
    selectable: Boolean = false,
    icon: DrawableResource? = null,
    onClick: () -> Unit = {}
) {
    if (selectable)
        SelectionContainer {
            RootChip(
                modifier = modifier,
                height = height,
                size = size,
                text = text,
                filled = filled,
                icon = icon,
                onClick = onClick
            )
        }
    else
        RootChip(
            modifier = modifier,
            height = height,
            size = size,
            text = text,
            filled = filled,
            icon = icon,
            onClick = onClick
        )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterChip(
    selected: Boolean,
    modifier: Modifier = Modifier,
    size: TextUnit,
    text: String,
    icon: DrawableResource? = null,
    onClick: () -> Unit,
){
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = size,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                lineHeight = size
            )
        },
        modifier = modifier,
        elevation = null,
        leadingIcon = {
            if (icon != null)
                Icon(
                    modifier = Modifier
                        .height(deviceValue(16, 32).dp),
                    painter = painterResource(icon),
                    contentDescription = null
                )
        },
    )
}