package ua.tiar.aim.ui.components

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.screens.InputPromptContent
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.colorOnView

@Composable
fun AnimatedBoxScreen(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    cancelable: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null) {
    if (isVisible && onDismiss != null) BackHandler {
        if (cancelable) onDismiss.invoke()
    }
//    val max = if (LocalConfiguration.current.screenWidthDp.dp > LocalConfiguration.current.screenHeightDp.dp) {
//        LocalConfiguration.current.screenWidthDp.dp - 100.dp
//    } else {
//        LocalConfiguration.current.screenWidthDp.dp
//    }
//    val max = LocalConfiguration.current.screenHeightDp.dp + 100.dp
    val interactionSource = remember { MutableInteractionSource() }
    AnimatedVisibility(
        modifier = Modifier.fillMaxSize(),
        visible = isVisible,
        enter = slideFromBottom(duration = 600).first,
        exit = slideFromBottom(duration = 600).second
    ) {
        val orientation = LocalConfiguration.current.orientation
        Box(
            modifier = modifier
//                .widthIn(0.dp, max)
                .fillMaxHeight()
                .padding(horizontal =
                    if (LocalConfiguration.current.screenWidthDp.dp > LocalConfiguration.current.screenHeightDp.dp) {
                        32.dp
                    } else {
                        8.dp
                    }
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (cancelable) onDismiss?.invoke()
                }.then(
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
//                    .widthIn(0.dp, max)
//                    .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    .dropShadow(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp), offsetY = (-4).dp, blur = 6.dp)
                    .align(Alignment.BottomCenter)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {}
                    .background(MaterialTheme.colorScheme.colorOnView, RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    .then(
                        if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
                            Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.BottomCenter) {
                content?.invoke()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAnimatedBoxScreen() {
    AIMuseTheme {
        AnimatedBoxScreen {
            InputPromptContent(modifier = Modifier.background(MaterialTheme.colorScheme.colorOnView))
        }
    }
}