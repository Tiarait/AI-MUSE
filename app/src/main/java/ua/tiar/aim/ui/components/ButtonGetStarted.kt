package ua.tiar.aim.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.tiar.aim.ui.theme.AIMuseTheme


@Composable
fun ButtonGetStarted(
    visible: Boolean = true,
    content: @Composable (() -> Unit) = {
        GetStartedButton()
    },
    onClicked: (() -> Unit)? = null) {

    val interactionSource = remember { MutableInteractionSource() }
//    val px = with(LocalDensity.current) { 140.dp.toPx().toInt() }
    AnimatedVisibility(
        modifier = Modifier,
        visible = visible,
        enter = slideFromBottom(coefficient = 2.5f, duration = 600, startDelay = 200, useShrink = false).first,
        exit = slideFromBottom(coefficient = 2.5f, duration = 600, useShrink = false).second,
    ) {
        Box {
            val brush = animatedBrush(this)
            OutlinedButton(
                onClick = {
                    if (visible) onClicked?.invoke()
                },
                modifier = Modifier
                    .fillMaxWidth()
//                .offset(y = animatedOffset, x = 1.dp)
                    .dropShadow(RoundedCornerShape(32.dp), offsetY = 0.dp, blur = 8.dp)
                    .pressClickEffect()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        if (visible) onClicked?.invoke()
                    },
                interactionSource = interactionSource,
                shape = RoundedCornerShape(32.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = BorderStroke(1.dp, brush)
            ) {
                content.invoke()
            }
        }

    }
}

@Composable
fun GetStartedButton(
    text: String = "Get started",
    visible: Boolean = true
) {
    val dots = remember { List(3) { Animatable(0.2f) } }

    LaunchedEffect(Unit) {
        dots.forEachIndexed { index, animatable ->
            launch {
                delay(index * 400L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutLinearInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.inversePrimary)
        ) {
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600))
                },
                label = "ButtonGetStartedText"
            ) { targetText ->
                Text(
                    text = targetText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        drawStyle = Stroke(
                            width = 2f,
                            join = StrokeJoin.Round
                        )
                    ),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
                Text(
                    text = targetText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            this@Row.AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth + fullWidth/2 },
                    animationSpec = tween(durationMillis = 600, delayMillis = 500)
                ),
                exit = slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth + fullWidth/2 },
                    animationSpec = tween(durationMillis = 600)
                )
            ) {
                Row(modifier = Modifier) {
                    dots.forEachIndexed { _, a ->
                        val alpha = a.value
                        val scale = (alpha + 0.7f).coerceAtMost(2.2f)
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = "",
                            modifier = Modifier
                                .alpha(alpha)
                                .scale(scale),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
            this@Row.AnimatedVisibility(
                visible = !visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = 500)),
                exit = fadeOut(animationSpec = tween(durationMillis = 600))
            ) {
                AnimatedAppIcon(
                    modifier = Modifier,
                    contentDescription = "get_started_btn_icon"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButtonGetStarted() {
    AIMuseTheme {
        ButtonGetStarted()
    }
}