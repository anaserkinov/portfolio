package com.anasmusa.portfolio.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.anasmusa.portfolio.utils.select
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Chip(
    modifier: Modifier = Modifier,
    height: Dp,
    size: TextUnit,
    text: String,
    copyable: Boolean = false,
    icon: DrawableResource? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(height/2))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(height/2))
            .let {
                if (onClick != null)
                    it.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                else
                    it
            }
            .padding(horizontal = select(6, 12).dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        if (icon != null)
            Icon(
                modifier = Modifier
                    .height(select(16, 32).dp),
                painter = painterResource(icon),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = null
            )
        Text(
            modifier = Modifier
                .wrapContentHeight()
                .padding(start = if (icon != null) select(2, 4).dp else 0.dp),
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = size,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            lineHeight = size
        )
    }
}