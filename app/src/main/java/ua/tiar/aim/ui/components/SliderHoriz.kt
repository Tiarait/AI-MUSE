package ua.tiar.aim.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun SliderHoriz(
    modifier: Modifier = Modifier,
    sliderPosition: MutableState<Float>? = null,
    from: Float = 0f,
    to: Float = 10f,
    step: Int = 1) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Slider(
            modifier = Modifier.weight(1f),
            value = sliderPosition?.value ?: 0f,
            onValueChange = { sliderPosition?.value = it },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onTertiary,
                activeTrackColor = MaterialTheme.colorScheme.onTertiary,
                inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            steps = step,
            valueRange = from..to
        )
//        Text(text = (sliderPosition?.value ?: 0f).toInt().toString(),
//            color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSliderHoriz() {
    AIMuseTheme {
        SliderHoriz()
    }
}