package ua.tiar.aim.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun SimpleOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    shape: Shape = RoundedCornerShape(32.dp),
    fontStyle: TextStyle = MaterialTheme.typography.labelMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    padding: Dp = 12.dp,
    elevation: Dp = 5.dp,
    ignoreEnabled: Boolean = false,
    onClicked: (() -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    OutlinedButton(
        onClick = { if (enabled || ignoreEnabled) onClicked?.invoke() },
        modifier = modifier
            .offset(x = 2.dp)
            .also {
                if (onClicked != null) {
                    it.pressClickEffect()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { if (enabled || ignoreEnabled) onClicked.invoke() }
                }
            },
        interactionSource = interactionSource,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer.adjustBrightness(if (enabled) 1f else 0.7f)),
        border = border,
        elevation = ButtonDefaults.elevatedButtonElevation(
            focusedElevation = elevation,
            hoveredElevation = elevation,
            defaultElevation = elevation,
            pressedElevation = if (onClicked != null) elevation/2 else elevation,
            disabledElevation = elevation,
        )
    ) {
        Text(text = text,
            style = fontStyle,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = fontWeight,
            modifier = Modifier
                .alpha(if (enabled) 1f else 0.4f)
                .padding(horizontal = padding * 2, vertical = padding))
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleOutlinedButton() {
    AIMuseTheme {
        SimpleOutlinedButton(text = "SimpleOutlinedButton")
    }
}