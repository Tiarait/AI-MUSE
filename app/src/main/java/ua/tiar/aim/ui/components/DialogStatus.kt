package ua.tiar.aim.ui.components

import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Adjust
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ua.tiar.aim.R
import ua.tiar.aim.data.models.StatusRequestModel
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.colorOnView
import ua.tiar.aim.ui.theme.gradientText

@Composable
fun DialogStatus(
    modifier: Modifier = Modifier,
    status: StatusRequestModel,
    time: Float = 0f,
    onDismissRequest: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val dialogTitle = if (status.action != -1) stringResource(id = status.action) else null
    val statusStatus = status.status ?: -1
    val message = StringBuilder()
    if (statusStatus != -1) message.append(ctx.getString(statusStatus))
    if (statusStatus != -1 && status.reason != null) message.append(":\n")
    if (status.reason != null) message.append(status.reason)
    val dialogText = if (message.isEmpty()) null else message.toString()
    
    val titleFun: @Composable (() -> Unit) = {
        if (dialogTitle != null) Box {
            Text(
                modifier = Modifier,
                text = dialogTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black,
                    drawStyle = Stroke(
                        width = 1f,
                        join = StrokeJoin.Round
                    )
                )
            )
            Text(
                modifier = Modifier,
                text = dialogTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    brush = MaterialTheme.colorScheme.gradientText
                )
            )
        }
    }
    val messageFun: @Composable (() -> Unit) = {
        if (dialogText != null) Text(text = dialogText, style = MaterialTheme.typography.bodyLarge.copy(
            lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.HighQuality)
        ), color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f))
    }
    Dialog(
        properties = DialogProperties(dismissOnBackPress = onDismissRequest != null, dismissOnClickOutside = onDismissRequest != null),
        onDismissRequest = { }//onDismissRequest?.invoke() }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(bottom = 16.dp, end = 16.dp, start = 16.dp)
            .clickable(
                interactionSource = null,
                indication = null
            ) { },
            contentAlignment = Alignment.Center) {
            val w = LocalConfiguration.current.screenWidthDp.dp - 64.dp
            val h = LocalConfiguration.current.screenHeightDp.dp - 64.dp
            Surface(
                modifier = modifier
                    .widthIn(min = if (w > h) h else w, max = if (w > h) w else h)
                    .dropShadow(
                        RoundedCornerShape(24.dp),
                        offsetY = 1.dp,
                        spread = 2.dp,
                        blur = 8.dp
                    ),
                shadowElevation = 5.dp,
                tonalElevation = 5.dp,
                color = MaterialTheme.colorScheme.colorOnView,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(top = 24.dp)
                            .padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AnimatedIconCircle(
                            modifier = Modifier
                                .size(if (dialogTitle != null && dialogText != null) 72.dp else 52.dp)
                                .padding(end = 16.dp)
                        )
                        Column {
                            if (dialogTitle != null) titleFun.invoke()
                            if (dialogTitle != null && dialogText != null) Spacer(
                                modifier = Modifier.height(
                                    16.dp
                                )
                            )
                            if (dialogText != null) messageFun.invoke()
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .widthIn(min = if (w > h) h else w, max = if (w > h) w else h),
                        contentAlignment = Alignment.Center
                    ) {
                        if (time != 0f) {
                            Text(
                                modifier = Modifier.padding(start = 8.dp)
                                    .align(Alignment.CenterStart),
                                text = time.toString(),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        SimpleOutlinedButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            text = stringResource(id = R.string.cancel_all)
                        ) {
                            onDismissRequest?.invoke()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedIconCircle(modifier: Modifier = Modifier) {
    Box {
//        val brush = brushGradientRainbow()//animatedBrush(this)
//        val brush = Brush.sweepGradient(
//            colors = listOf(Color.Cyan, ua.tiar.aim.ui.theme.Purple, ua.tiar.aim.ui.theme.Pink, ua.tiar.aim.ui.theme.Purple, ua.tiar.aim.ui.theme.Green, Color.Cyan),
//        )
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val v by infiniteTransition.animateFloat(
            initialValue = .3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = EaseInBounce),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        val brush = animatedBrush(this, -v)
        Image(
            imageVector = Icons.Rounded.Adjust,
            modifier = modifier
//                .rotate(-v*360)
                .align(Alignment.TopEnd)
                .scale(v / 3 * 2 + 0.8f)
//                .alpha(alpha)
                .graphicsLayer(
                    alpha = .9f
                )
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        rotate(degrees = v * 2 * 360) {
                            drawCircle(
                                brush = brush,
                                radius = size.width,
                                blendMode = BlendMode.SrcIn,
                            )
                        }
                    }
                },
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            contentDescription = "dashboard_search"
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDialogStatus() {
    AIMuseTheme {
        DialogStatus(status = StatusRequestModel(R.string.app_name, reason = "dialog test"), time = 22f)
    }
}