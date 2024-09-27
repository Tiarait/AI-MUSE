package ua.tiar.aim.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun FadeBackground(visible: Boolean = false) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 600)),
        exit = fadeOut(animationSpec = tween(durationMillis = 600)),
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)))
    }

}


@Preview(showBackground = true)
@Composable
fun PreviewFadeBackground() {
    AIMuseTheme {
        FadeBackground()
    }
}