package ua.tiar.aim.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun TypewriteText(
    text: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    contentAlignment: Alignment = Alignment.TopStart,
    spec: AnimationSpec<Int> = tween(durationMillis = text.length * 100, easing = LinearEasing),
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    outlineWidth: Float = 0f,
    preoccupySpace: Boolean = true
) {
    // State that keeps the text that is currently animated
    val textToAnimate = remember { mutableStateOf("") }

    // Animatable index to control the progress of the animation
    val index = remember {
        Animatable(initialValue = 0, typeConverter = Int.VectorConverter)
    }

    // Effect to handle animation when visibility changes
    LaunchedEffect(isVisible) {
        if (isVisible) {
            textToAnimate.value = text
            index.animateTo(text.length, spec)
        } else {
            index.snapTo(0)
        }
    }

    // Effect to handle animation when text content changes
    LaunchedEffect(text) {
        if (isVisible) {
            index.snapTo(0)
            textToAnimate.value = text
            index.animateTo(text.length, spec)
        }
    }

    // Box composable to contain the animated and static text
    Box(modifier = modifier, contentAlignment = contentAlignment) {
        if (preoccupySpace && index.isRunning) {
            // Display invisible text when preoccupation is turned on
            // and the animation is in progress.
            // Plays the role of a placeholder to occupy the space
            // that will be filled with text.
            Text(
                text = text,
                style = style,
                modifier = Modifier.alpha(0f)
            )
        }

        // Display animated text based on the current index value
        if (outlineWidth > 0f) {
            Text(
                text = textToAnimate.value.substring(0, index.value),
                style = style.copy(
                    color = Color.Black,
                    drawStyle = Stroke(
                        width = outlineWidth,
                        join = StrokeJoin.Round
                    )
                )
            )
        }
        Text(
            text = textToAnimate.value.substring(0, index.value),
            style = style
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTypewriteText() {
    AIMuseTheme {
        TypewriteText(text = "TypewriteText TypewriteText")
    }
}