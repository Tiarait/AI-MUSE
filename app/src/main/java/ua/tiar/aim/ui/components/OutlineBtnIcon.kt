package ua.tiar.aim.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun OutlineBtnIcon(
    modifier: Modifier = Modifier,
    @DrawableRes drawable: Int? = null,
    imageVector: ImageVector? = null,
    text:String = "",
    size: Dp = 64.dp,
    iconColor: Color = MaterialTheme.colorScheme.outline,
    isSelected: Boolean = false,
    isTextInside: Boolean = false,
    onClicked: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    val mModifier = modifier.pressClickEffect()
    Column {
        OutlinedButton(
            onClick = { onClicked?.invoke() },
            modifier = mModifier
                .offset(x = 1.dp)
                .size(size)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onClicked?.invoke() },
            interactionSource = interactionSource,
            shape = RoundedCornerShape(4.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.outline)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (imageVector != null || drawable != null) {
                    Image(
                        painter = if (imageVector != null)
                            rememberVectorPainter(image = imageVector) else
                            painterResource(id = drawable!!),
                        contentDescription = null,
                        modifier = Modifier
                            .size(size)
                            .padding(15.dp)
                            .alpha(if (isSelected) 1f else .5f)
                            .align(Alignment.Center),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                } else if (isTextInside && text.isNotEmpty()) {
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        maxLines = 3,
                        modifier = Modifier
                            .width(size)
                            .padding(horizontal = 6.dp)
                            .alpha(if (isSelected) 1f else .5f)
                    )
                }

            }
        }
        if (!isTextInside && text.isNotEmpty()) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 2,
                modifier = Modifier
                    .width(size)
                    .alpha(if (isSelected) 1f else .5f)
            )
        }
    }

}

@Preview(showBackground = false)
@Composable
fun PreviewOutlineBtnIcon() {
    AIMuseTheme {
        OutlineBtnIcon(text = "Button", isTextInside = true)
    }
}