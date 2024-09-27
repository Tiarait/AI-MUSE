package ua.tiar.aim.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ua.tiar.aim.Utils
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun AspectBtn(
    modifier: Modifier = Modifier,
    width: Int = 512,
    height: Int = 512,
    size: Dp = 64.dp,
    isSelected: Boolean = false,
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
            Column(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .padding(8.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .aspectRatio(width / height.toFloat())
                        .weight(1f)
                        .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                        .alpha(if (isSelected) 1f else .5f)
                )
                val (w, h) = Utils.calculateAspectRatio(width, height)
                Text(text = "${w}:$h",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .alpha(if (isSelected) 1f else .5f))
            }
        }
        Text(text = "${width}x$height",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 2,
            modifier = Modifier
                .width(size)
                .alpha(if (isSelected) 1f else .5f))
    }

}

@Preview(showBackground = false)
@Composable
fun PreviewAspectBtn() {
    AIMuseTheme {
        AspectBtn(width = 512, height =  768)
    }
}