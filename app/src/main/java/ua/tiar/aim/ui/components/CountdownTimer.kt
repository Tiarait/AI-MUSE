package ua.tiar.aim.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CountdownTimer(
    timeSec: Int = 10,
    onTick: ((Int) -> Unit)? = null,
    onFinish: (() -> Unit)? = null) {
    var timeLeft by remember { mutableIntStateOf(timeSec) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
                onTick?.invoke(timeLeft)
            }
            onFinish?.invoke()
        }
    }
}