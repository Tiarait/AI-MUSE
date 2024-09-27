package ua.tiar.aim.ui.components

import android.content.res.Configuration
import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun BackCover(modifier: Modifier = Modifier) {
    val contrast = .7f // 0f..10f (1 should be default)
    val brightness = -40f // -255f..255f (0 should be default)
    val colorMatrix = floatArrayOf(
        contrast, 0f, 0f, 0f, brightness,
        0f, contrast, 0f, 0f, brightness,
        0f, 0f, contrast, 0f, brightness,
        0f, 0f, 0f, 1f, 0f
    )
    Box(modifier = modifier
        .fillMaxSize()) {
        val orientation = LocalConfiguration.current.orientation
        var draw = R.drawable.p_h_0
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            val draws = listOf(R.drawable.p_v_0, R.drawable.p_v_1, R.drawable.p_v_2)
            draw = draws[draws.indices.random()]
        }
        val drawId by remember { mutableIntStateOf(draw) }
        Image(
            painter = painterResource(id = drawId),
                colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix)),
            contentDescription = "cover",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
//                .alpha(0.8f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = .4f),
                            Color.Transparent
                        ),
                    )
                )
                .align(Alignment.TopCenter)
        ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(16.dp))
        }

        Box(
            modifier = Modifier
                .height(LocalConfiguration.current.screenHeightDp.dp)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                    )
                )
                .align(Alignment.BottomCenter)
        )
    }
}

enum class ButtonState { Pressed, Idle }
fun Modifier.pressClickEffect(
    dx: Float = -5f,
    dy: Float = -5f,
    state: ButtonState? = null,
    onStateChanged: ((ButtonState) -> Unit)? = null
) = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val ty by animateFloatAsState(
        if (buttonState == ButtonState.Pressed || state == ButtonState.Pressed) 0f else dy,
        label = ""
    )
    val tx by animateFloatAsState(
        if (buttonState == ButtonState.Pressed || state == ButtonState.Pressed) 0f else dx,
        label = ""
    )

    var prevButtonState by remember { mutableStateOf(ButtonState.Idle) }
    if (buttonState != prevButtonState) {
        onStateChanged?.invoke(buttonState)
        prevButtonState = buttonState
    }

    this
        .padding(2.dp)
        .graphicsLayer {
            translationY = ty
            translationX = tx
        }
//        .clickable(true) {
//            onClicked?.invoke()
//        }
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

@Composable
fun animatedBrush(scope: BoxScope, offset: Float? = null): Brush {
    val isDark = isSystemInDarkTheme()
    return with(scope) {
        var mOffset = offset
        if (mOffset == null) {
            val infiniteTransition = rememberInfiniteTransition(label = "")
            val v by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            mOffset = v
        }
        remember(mOffset) {
            object : ShaderBrush() {
                override fun createShader(size: Size): Shader {
                    val widthOffset = size.width * mOffset
                    val heightOffset = size.height * mOffset
                    return LinearGradientShader(
                        colors = if (isDark)
                            listOf(Color.Cyan, Color(0xFF8F70E6), Color(0xFFFF29AA), Color.Cyan,)
                        else listOf(
                            Color(0xFFD50000),
                            Color(0xFF0091EA),
                            Color(0xFFFFD600),
                            Color(0xFF000000),
                            Color(0xFFE930FE),
                            Color(0xFFFFDD00)
                        ),
                        from = Offset(widthOffset, heightOffset),
                        to = Offset(widthOffset + size.width, heightOffset + size.height),
                        tileMode = TileMode.Mirror
                    )
                }
            }
        }
    }

}

@Composable
fun getBottomLineShape(lineThicknessDp: Dp) : Shape {
    val lineThicknessPx = with(LocalDensity.current) {lineThicknessDp.toPx()}
    return GenericShape { size, _ ->
        // 1) Bottom-left corner
        moveTo(0f, size.height)
        // 2) Bottom-right corner
        lineTo(size.width, size.height)
        // 3) Top-right corner
        lineTo(size.width, size.height - lineThicknessPx)
        // 4) Top-left corner
        lineTo(0f, size.height - lineThicknessPx)
    }
}

fun Modifier.dropShadow(
    shape: Shape,
    color: Color = Color.Black.copy(0.35f),
    blur: Dp = 4.dp,
    offsetY: Dp = 4.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0.dp
) = this.drawBehind {

    val shadowSize = Size(size.width + spread.toPx(), size.height + spread.toPx())
    val shadowOutline = shape.createOutline(shadowSize, layoutDirection, this)

    val paint = Paint()
    paint.color = color

    if (blur.toPx() > 0) {
        paint.asFrameworkPaint().apply {
            maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
        }
    }

    drawIntoCanvas { canvas ->
        canvas.save()
        canvas.translate(offsetX.toPx(), offsetY.toPx())
        canvas.drawOutline(shadowOutline, paint)
        canvas.restore()
    }
}

fun slideFromBottom(coefficient: Float = 1f, useShrink: Boolean = true, duration: Int = 400, startDelay: Int = 0, endDelay: Int = 0): Pair<EnterTransition, ExitTransition> {
    var enterAnim = slideInVertically (animationSpec = tween(durationMillis = duration, delayMillis = startDelay)) {
        (it * coefficient).toInt()
    }
    if (useShrink) enterAnim += expandVertically(animationSpec = tween(durationMillis = duration, delayMillis = startDelay)) { it }
    var exitAnim = slideOutVertically (animationSpec = tween(durationMillis = duration, delayMillis = endDelay)) {
        (it * coefficient).toInt()
    }
    if (useShrink) exitAnim += shrinkVertically(animationSpec = tween(durationMillis = duration, delayMillis = endDelay)) { it }
    return enterAnim to exitAnim
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedBoundsModifier(
    round: Dp = 16.dp,
    sharedScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    sharedContentState: SharedTransitionScope.SharedContentState
): Modifier {
//    val itemBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
//        keyframes {
//            durationMillis = 200
//            initialBounds at 0 using ArcMode.ArcLinear using FastOutSlowInEasing
//            targetBounds at 200
//        }
//    }
    val shape = RoundedCornerShape(round)
    with(sharedScope) {
        return this@sharedBoundsModifier
            .sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
//                boundsTransform = itemBoundsTransform,
//                enter = fadeIn(tween(durationMillis = 400)),
//                exit = fadeOut(tween(durationMillis = 400)),
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransition = OverlayClip(shape),
                renderInOverlayDuringTransition = false
            )
            .clip(shape)
            .background(Color.Transparent, shape)
    }
}

fun swipeNestedScrollConnection(
    scrollState: LazyStaggeredGridState,
    offsetY: MutableFloatState,
    maxOffset: Float,
    enabled: Boolean,
    onRefresh: (() -> Unit)? = null
): NestedScrollConnection {
    val isBlocked = mutableStateOf(false)
    return object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (!isBlocked.value && enabled) {
                if (!scrollState.canScrollBackward || offsetY.floatValue > 0f) {
//                    offsetY.floatValue = (offsetY.floatValue + available.y).coerceAtMost(maxOffset)
                    if (available.y > 0) {
                        offsetY.floatValue = (offsetY.floatValue + available.y).coerceAtMost(maxOffset)
                        return Offset(0f, available.y)
                    } else {
                        offsetY.floatValue += (available.y * 3f)
                        return Offset(0f, available.y/2)
                    }
                } else {
                    offsetY.floatValue = 0f
                }
            }
            return super.onPreScroll(available, source)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (offsetY.floatValue >= maxOffset - maxOffset/10 && enabled) {
                onRefresh?.invoke()
            }
            offsetY.floatValue = 0f
            isBlocked.value = true
            return super.onPreFling(available)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (isBlocked.value) {
                isBlocked.value = false
            }
            offsetY.floatValue = 0f
            return super.onPostFling(consumed, available)
        }
    }
}

fun Color.adjustBrightness(factor: Float): Color {
    return if (factor == 1f) this else this.copy(
        red = (red * factor).coerceIn(0f, 1f),
        green = (green * factor).coerceIn(0f, 1f),
        blue = (blue * factor).coerceIn(0f, 1f)
    )
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    AIMuseTheme {
        BackCover()
    }
}