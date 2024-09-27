package ua.tiar.aim.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.gradientText

@Composable
fun FirstScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()) {
        Box(modifier = Modifier.weight(8f))
        Column(modifier = Modifier) {
            Box(Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()) {
                Text(text = stringResource(id = R.string.app_slogan),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.Black,
                        drawStyle = Stroke(
                            width = 3f,
                            join = StrokeJoin.Round
                        ),
                        lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.HighQuality)
                    ))
                Text(text = stringResource(id = R.string.app_slogan),
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = MaterialTheme.colorScheme.gradientText,
                        lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.HighQuality)
                    ))
            }
            Box(Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .fillMaxWidth()) {
                Text(text = stringResource(id = R.string.app_slogan_d),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        drawStyle = Stroke(
                            width = 2f,
                            join = StrokeJoin.Round
                        ),
                        lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.HighQuality)
                    ))
                Text(text = stringResource(id = R.string.app_slogan_d),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        brush = MaterialTheme.colorScheme.gradientText,
                        lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.HighQuality)
                    ))
            }


            Spacer(modifier = Modifier.height(110.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFirstScreen() {
    AIMuseTheme {
        FirstScreen()
    }
}