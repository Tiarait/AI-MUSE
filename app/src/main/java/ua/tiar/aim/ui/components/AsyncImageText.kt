package ua.tiar.aim.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AsyncImageText(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    text: String = "",
    alpha: Float = 1f,
    imageRequest: ImageRequest? = null,
    imageVector: ImageVector? = null,
    isLoading: Boolean = false,
    isSelected: Boolean = false,
    selectedMode: Boolean = false,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable (() -> Unit) = { },
    onLongClick: (() -> Unit)? = null,
    onClicked: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val backgroundColor = if (selectedMode && isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.outline
    val borderWidth = if (selectedMode && isSelected) 2.dp else 1.dp
    val alphaValue = if (selectedMode && !isSelected) 0.3f else 1f

    OutlinedButton(
        onClick = onClicked ?: {},
        modifier = modifier
            .offset(1.dp, 1.dp)
            .alpha(alphaValue)
            .pressClickEffect(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(width = borderWidth, color = backgroundColor),
        shape = shape,
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .alpha(alpha)
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = false, color = Color.White),
                    onClick = { onClicked?.invoke() },
                    onLongClick = onLongClick
                )
        ) {
            when {
                imageRequest != null -> AsyncImage(
                    model = imageRequest,
                    contentDescription = "img",
                    error = painterResource(id = R.drawable.broken_image),
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer, shape)
                )
                !isLoading -> Image(
                    imageVector = imageVector ?: Icons.Rounded.BrokenImage,
                    contentDescription = "img",
                    contentScale = ContentScale.Fit,
                    modifier = imageModifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                )
                else -> Box(
                    modifier = imageModifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer, shape)
                )
            }
            content.invoke()
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color(0x80000000))
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewAsyncImageText() {
    AIMuseTheme {
        AsyncImageText(modifier = Modifier
            .height(310.dp)
            .width(210.dp), text = "AsyncImageText")
    }
}