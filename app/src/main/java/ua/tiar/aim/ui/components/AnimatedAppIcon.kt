package ua.tiar.aim.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedAppIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    contentDescription: String? = null) {
    Box(modifier = modifier) {
        val brush = animatedBrush(this)
        Image(
            imageVector = imageVector ?: Icons.Rounded.BlurOn,
            modifier = Modifier
                .height(32.dp)
                .width(32.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer(
                    alpha = .9f
                )
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        rotate(degrees = 20f) {
                            drawCircle(
                                brush = brush,
                                radius = size.width,
                                blendMode = BlendMode.SrcIn,
                            )
                        }
                    }
                },
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            contentDescription = contentDescription
        )
    }
}