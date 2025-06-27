package me.anasmusa.portfolio.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import me.anasmusa.portfolio.core.select

@Composable
fun LineColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    Column(
        modifier = modifier
            .padding(start = 18.dp)
            .drawBehind {
                drawLine(
                    color = onBackground,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(top = select(20, 40).dp)
            .drawBehind {
                drawCircle(
                    color = onBackground,
                    radius = 9.dp.toPx(),
                    center = Offset(0f, 12.dp.toPx())
                )
            },
        content = content
    )
}