package me.anasmusa.portfolio.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.anasmusa.portfolio.core.isTablet
import me.anasmusa.portfolio.core.stringResource

@Composable
fun InfoCell(
    date: String,
    title: String,
    items: List<Int>
) {
    val onBackground = MaterialTheme.colorScheme.onBackground
    LineColumn (
        modifier = Modifier
    ) {
        val dateSize: TextUnit
        val titleSize: TextUnit
        val descriptionSize: TextUnit
        val top: Dp
        val bottom: Dp

        if (isTablet()){
            dateSize = 15.sp
            titleSize = 22.sp
            descriptionSize = 17.sp
            top = 4.dp
            bottom = 4.dp
        } else {
            dateSize = 11.sp
            titleSize = 13.sp
            descriptionSize = 11.sp
            top = 2.dp
            bottom = 12.dp
        }

        Text(
            modifier = Modifier
                .padding(start = 20.dp),
            text = date,
            color = MaterialTheme.colorScheme.secondary,
            fontSize =dateSize,
            fontWeight = FontWeight.Medium
        )
        Text(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    top = top,
                    bottom = bottom
                ),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = titleSize,
            fontWeight = FontWeight.SemiBold
        )
        items.forEach {
            TextWithHeight(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .drawBehind {
                        drawLine(
                            color = onBackground,
                            start = Offset(0f, size.height/2),
                            end = Offset(16.dp.toPx(), size.height/2),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .padding(start = 32.dp),
                text = stringResource(it),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = descriptionSize,
                fontWeight = FontWeight.Medium
            )
        }
    }
}