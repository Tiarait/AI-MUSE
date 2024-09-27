package ua.tiar.aim.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PullToRefreshIndicator(
    swipeOffset: Float = 0f,
    isLoading: Boolean = false,
    maxOffset: Float = 200f
) {
    val indicatorSize = 64.dp
    val screenHeightPx = LocalContext.current.resources.displayMetrics.heightPixels -
            with(LocalDensity.current) { indicatorSize.toPx() * 2 }
    var targetValue by remember { mutableFloatStateOf(if (isLoading) screenHeightPx / 2f else 0f) }
    val animatedOffset by animateFloatAsState(
        targetValue = targetValue, label = "pullIndicatorOffset",
        animationSpec = tween(durationMillis = if (targetValue == 0f || isLoading) 600 else 50)
    )
    LaunchedEffect(swipeOffset, isLoading) {
        if (isLoading) {
            targetValue = screenHeightPx / 2f
        } else {
            if (swipeOffset == 0f) delay(150)
            targetValue = swipeOffset
        }
    }
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = targetValue > 0f,
        enter = fadeIn(),
        exit = fadeOut() + shrinkVertically()
    ) {
//        val h = with(LocalDensity.current) { maxOffset.toDp() }
        Box(modifier = Modifier
            .statusBarsPadding()
//            .padding(top = AppConstants.topBarHeight)
            .fillMaxSize()
//            .height(h),
                ,
            contentAlignment = Alignment.TopCenter
        ) {
            val c = if (targetValue > 0f) min((targetValue / maxOffset).pow(5f), 1f) else 0.01f
            val cL = if (targetValue > 0f) min((targetValue / maxOffset), 1f) else 0.01f
            ElevatedCard(
                modifier = Modifier.size(indicatorSize)
                    .scale(1f * cL)
//                    .offset(y = -h - 10.dp)
                    .offset {
                        IntOffset(0, animatedOffset.roundToInt())
                    }
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outline, shape = CircleShape),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = CircleShape
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    var r = c * 360
                    if (isLoading || targetValue == 0f || targetValue >= maxOffset - (maxOffset/10)) {
                        val infiniteTransition = rememberInfiniteTransition(label = "")
                        val v by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ), label = ""
                        )
                        r = v * 360
                    }
                    SineWaveAnimation(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(c)
                            .offset {
                                val y = if (isLoading || targetValue == 0f) 0.dp
                                else indicatorSize - indicatorSize * c
                                IntOffset(x = 0, y = y.roundToPx())
                            },
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    Image(
                        painter = rememberVectorPainter(image = Icons.Rounded.Refresh),
                        contentDescription = "pullIndicatorRefresh",
                        modifier = Modifier
                            .size(indicatorSize)
                            .alpha(c)
                            .rotate(r)
                            .scale(c)
                            .padding(12.dp)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
                    )
                }
            }
        }
    }
}

@Composable
fun SineWaveAnimation(modifier: Modifier = Modifier, color: Color) {
    val transition = rememberInfiniteTransition(label = "transitionSineWaveOffset")
    val sineWaveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing)
        ), label = "sineWaveOffset"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val waveFrequency = 0.02f
        val waveAmplitude = height / 4
        val path = Path().apply {
            moveTo(0f, height / 2)
            for (x in 0 until width.toInt()) {
                val y = waveAmplitude * sin(waveFrequency * x + sineWaveOffset)
                lineTo(x.toFloat(), (height / 2) - y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Fill
        )
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2f)
        )
    }
}

