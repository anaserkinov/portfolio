package me.anasmusa.portfolio.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BouncingDots(
    dotSize: Dp = 12.dp,
    spaceBetween: Dp = 8.dp,
    travelDistance: Dp = 8.dp,
    animationDuration: Int = 400
) {
    val dotColor = MaterialTheme.colorScheme.onBackground

    val scope = rememberCoroutineScope()
    val dots = List(3) { remember { Animatable(0f) } }
    val travelPx = with(LocalDensity.current) { -travelDistance.toPx() }

    LaunchedEffect(Unit) {
        dots.forEachIndexed { index, anim ->
            scope.launch {
                delay(index * animationDuration / dots.size.toLong())
                anim.animateTo(
                    targetValue = travelPx,
                    animationSpec = infiniteRepeatable(
                        animation = tween(animationDuration, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentSize()
            .padding(12.dp)
    ) {
        dots.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer {
                        translationY = anim.value
                    }
                    .background(dotColor, shape = CircleShape)
            )
        }
    }
}
