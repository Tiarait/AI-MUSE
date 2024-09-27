package ua.tiar.aim.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme


@Composable
fun CircleElevatedBtn(
    modifier: Modifier = Modifier,
    modifierContainer: Modifier = Modifier,
    @DrawableRes drawable: Int = R.drawable.ic_crown_v,
    imageVector: ImageVector? = null,
    size: Dp = 44.dp,
    height: Dp? = null,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    paddingIcon: Dp = 3.dp,
    elevation: Dp = 5.dp,
    isTransparent: Boolean = false,
    borderStroke: BorderStroke? = null,
    text: String? = null,
    contentDescription: String? = null,
    onClicked: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    val mSize = height ?: size
    val mModifier = if (height != null) {
        modifier
            .fillMaxWidth()
            .height(height)
    } else modifier.size(size)
    Column(
        modifier = modifierContainer,
        horizontalAlignment = Alignment.CenterHorizontally) {
        ElevatedButton(
            onClick = {
                onClicked?.invoke()
            },
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults
                .buttonColors(containerColor = if (!isTransparent) backgroundColor else Color.Transparent),
            modifier = mModifier
                .apply {
                    if (onClicked != null) {
                        padding(start = 2.dp, top = 2.dp)
                        pressClickEffect()
                    }
                },
            enabled = onClicked != null,
            shape = CircleShape,
            border = borderStroke,
            interactionSource = interactionSource,
            elevation = if (!isTransparent) ButtonDefaults.elevatedButtonElevation(
                focusedElevation = elevation + 2.dp,
                defaultElevation = elevation,
                pressedElevation = elevation - 2.dp
            ) else null
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()

            ) {
                Image(
                    painter = if (imageVector != null) rememberVectorPainter(image = imageVector) else painterResource(id = drawable),
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .size(mSize)
                        .padding(paddingIcon)
                        .align(Alignment.Center),
                    colorFilter = ColorFilter.tint(iconColor)
                )
            }
        }
        if (text != null)
            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .alpha(.6f)
                    .padding(top = 2.dp),
                text = text,
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center)
    }

}

@Preview(showBackground = false)
@Composable
fun PreviewCircleElevatedBtn() {
    AIMuseTheme {
        CircleElevatedBtn(text = "Longtextline")
    }
}