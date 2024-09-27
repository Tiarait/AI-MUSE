package ua.tiar.aim

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ua.tiar.aim.ui.components.CountdownTimer
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.gradientText

class AdsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.bg))
//        enableEdgeToEdge()
        setContent {
            AIMuseTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val orientation = LocalConfiguration.current.orientation
    Box(modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        val timeMax = 10
        var timeLeft by remember { mutableStateOf(timeMax.toString()) }
        val context = LocalContext.current
        CountdownTimer(
            timeSec = timeMax,
            onTick = {
                timeLeft = it.toString()
            }, onFinish = {
                (context as? ComponentActivity)?.finish()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    (context as? ComponentActivity)?.overrideActivityTransition(
                        OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out, Color.Transparent.toArgb())
                } else {
                    @Suppress("DEPRECATION")
                    (context as? ComponentActivity)?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            })
        Box(modifier.fillMaxSize()
            .statusBarsPadding()
            .then(
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                } else {
                    Modifier
                }
            )) {
            Box(modifier = Modifier.size(90.dp)
                .padding(20.dp)
                .align(Alignment.TopEnd)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (timeLeft != "0") timeLeft else "X",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = stringResource(id = R.string.ads_text),
                modifier = Modifier
                    .padding(32.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(brush = MaterialTheme.colorScheme.gradientText).copy(
                    color = Color.Black,
                    drawStyle = Stroke(
                        width = 3f,
                        join = StrokeJoin.Round
                    )
                ),
            )
            Text(
                text = stringResource(id = R.string.ads_text),
                modifier = Modifier
                    .padding(32.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(brush = MaterialTheme.colorScheme.gradientText),
            )
        }

    }
    BackHandler { }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AIMuseTheme {
        Greeting()
    }
}