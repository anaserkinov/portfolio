package me.anasmusa.portfolio.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot

sealed class AnimationType(val inverse: Boolean){
    class CIRCULAR_REVEAL(
        val center: Offset,
        val initialRadius: Int,
        inverse: Boolean = false
    ): AnimationType(inverse)
    class LRT(inverse: Boolean = false): AnimationType(inverse)
}

@Composable
fun AnimationOverlay(
    snapshotImage: ImageBitmap?,
    animationType: AnimationType?,
    onAnimationFinished: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    snapshotImage?.let { image ->
        var animatedProgress by remember { mutableStateOf(0f) }
        coroutineScope.launch {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(500, easing = LinearEasing)
            ) { value, _ ->
                animatedProgress = value
                if (value == 1f)
                    coroutineScope.launch {
                        delay(100)
                        onAnimationFinished()
                    }
            }
        }
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                if (animationType is AnimationType.CIRCULAR_REVEAL) {
                    val maxRadius = hypot(size.width, size.height)

                    if (animationType.inverse) {
                        val currentRadius = maxRadius * (1f - animatedProgress)
                        //clear outside of circle
                        drawCircle(
                            color = Color.Black,
                            radius = currentRadius,
                            center = Offset(
                                animationType.center.x + animationType.initialRadius,
                                animationType.center.y + animationType.initialRadius
                            )
                        )
                        drawImage(
                            image = image,
                            topLeft = Offset.Zero,
                            blendMode = BlendMode.SrcIn
                        )
                    } else {
                        val currentRadius = maxRadius * animatedProgress
                        //clear circle
                        drawImage(
                            image = image,
                            topLeft = Offset.Zero,
                        )

                        drawCircle(
                            color = Color.Black,
                            radius = currentRadius,
                            center = Offset(
                                animationType.center.x + animationType.initialRadius,
                                animationType.center.y + animationType.initialRadius
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                } else {
                    val currentWidth = size.width * animatedProgress
                    drawImage(
                        image = image,
                        topLeft = Offset.Zero,
                    )

                    drawRect(
                        color = Color.Black,
                        topLeft = Offset.Zero,
                        size = Size(currentWidth, size.height),
                        blendMode = BlendMode.Clear
                    )
                }
                restoreToCount(checkPoint)
            }
        }
    }

}