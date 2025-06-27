package me.anasmusa.portfolio.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalTime
import me.anasmusa.portfolio.component.TextWithHeight
import me.anasmusa.portfolio.core.select

@Composable
fun MessageCell(
    type: Message.Type,
    text: String,
    time: LocalTime,
    maxWith: Float
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        val background: Color
        val content: Color
        if (type == Message.Type.USER){
            background = MaterialTheme.colorScheme.primary
            content = MaterialTheme.colorScheme.onPrimary
        } else {
            background = MaterialTheme.colorScheme.surface
            content = MaterialTheme.colorScheme.onSurface
        }
        Column(
            modifier = Modifier
                .widthIn(min = (maxWith* 0.2).dp, max = (maxWith * 0.8).dp)
                .clip(RoundedCornerShape(16.dp))
                .background(background)
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .let {
                    if (type == Message.Type.USER)
                        it.align(Alignment.CenterEnd)
                    else
                        it.align(Alignment.CenterStart)
                }
        ){
            SelectionContainer {
                Text(
                    modifier = Modifier
                        .wrapContentWidth(),
                    text = text,
                    color = content,
                    fontSize = select(15, 17).sp
                )
            }
            TextWithHeight(
                modifier = Modifier
                    .align(Alignment.End),
                text = "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}",
                color = content.copy(alpha = 0.85f),
                fontSize = select(10, 12).sp
            )
        }
    }

}