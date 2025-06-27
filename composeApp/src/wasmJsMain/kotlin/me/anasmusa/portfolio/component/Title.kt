package me.anasmusa.portfolio.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.anasmusa.portfolio.core.select
import me.anasmusa.portfolio.core.stringResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun Title(
    icon: DrawableResource,
    title: Int
){
    Row(
        modifier = Modifier
            .padding(bottom = select(16, 25).dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            modifier = Modifier.padding(start = select(4, 8).dp),
            text = stringResource(title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = select(20, 33).sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}