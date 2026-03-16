package me.anasmusa.portfolio.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import kotlinx.browser.document
import kotlinx.browser.window
import me.anasmusa.portfolio.api.model.AboutResponse
import me.anasmusa.portfolio.api.model.LinkType
import me.anasmusa.portfolio.component.Chip
import me.anasmusa.portfolio.component.ShimmerBox
import me.anasmusa.portfolio.core.deviceValue
import me.anasmusa.portfolio.core.textAndIcon
import me.anasmusa.portfolio.core.withDownloadBaseUrl
import org.w3c.dom.HTMLAnchorElement

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutSection(
    modifier: Modifier,
    data: AboutResponse?
) {
    val modifier = modifier.fillMaxWidth()
        .padding(
            top = deviceValue(80, 200).dp,
            bottom = deviceValue(150, 250).dp
        )

    data?.let { data ->
        Column(modifier = modifier) {

            AsyncImage(
                data.photoPath.withDownloadBaseUrl(),
                contentDescription = null,
                modifier = Modifier
                    .size(deviceValue(53, 89).dp)
                    .clip(CircleShape),
            )

            Text(
                text = data.welcomeMessage,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = deviceValue(25, 52).sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                modifier = Modifier
                    .padding(top = deviceValue(8, 10).dp),
                text = data.about,
                color = MaterialTheme.colorScheme.secondary,
                fontSize = deviceValue(10, 24).sp,
                fontWeight = FontWeight.Bold
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = deviceValue(12, 24).dp),
                horizontalArrangement = Arrangement.spacedBy(deviceValue(12, 28).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val height = deviceValue(22, 44).dp
                val size = deviceValue(8, 19).sp
                data.contact.forEach {
                    val (text, icon) = it.textAndIcon()
                    Chip(
                        height = height,
                        size = size,
                        text = if (it.type == LinkType.Mail) it.value
                        else text,
                        icon = icon,
                        onClick = {
                            when (it.type) {
                                LinkType.Mail -> {
                                    window.open("mailto:${it.value}?body=Hi%20${data.firstName},", "_parent")
                                }
                                LinkType.CV -> {
                                    val link = document.createElement("a") as HTMLAnchorElement
                                    val url = it.value.withDownloadBaseUrl()
                                    link.href = url
                                    link.download = url
                                    link.click()
                                }
                                else -> {
                                    window.open(it.value, "_blank")
                                }
                            }
                        }
                    )
                }
            }
        }
    } ?: run {
        Column(
            modifier = modifier
                .shimmer()
        ) {

            ShimmerBox(
                modifier = Modifier
                    .size(deviceValue(53, 89).dp),
                shape = CircleShape
            )

            ShimmerBox(
                modifier = Modifier
                    .padding(top = deviceValue(8, 16).dp)
                    .fillMaxWidth(0.3f)
                    .height(deviceValue(26, 40).dp),
            )

            ShimmerBox(
                modifier = Modifier
                    .padding(top = deviceValue(8, 16).dp)
                    .fillMaxWidth(0.6f)
                    .height(deviceValue(26, 40).dp),
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(deviceValue(12, 28).dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val height = deviceValue(22, 44).dp
                repeat(4){
                    ShimmerBox(
                        modifier = Modifier
                            .width(deviceValue(80, 200).dp)
                            .height(height),
                        shape = RoundedCornerShape(height/2)
                    )
                }
            }
        }
    }
}