package ua.tiar.aim.ui.screens

import android.app.Activity.OVERRIDE_TRANSITION_OPEN
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import ua.tiar.aim.AdsActivity
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils
import ua.tiar.aim.network.perchance.ApiRoutes
import ua.tiar.aim.ui.components.AnimatedBoxScreen
import ua.tiar.aim.ui.components.DialogAlert
import ua.tiar.aim.ui.components.DialogStatus
import ua.tiar.aim.ui.components.WebView
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.viewmodel.AppViewModel
import kotlin.math.round

@Composable
fun ComposeApp(appViewModel: AppViewModel = viewModel()) {
    val tokenIsRequired by appViewModel.tokenRequired.collectAsState()
    val alertDialogs by appViewModel.alertDialogs.collectAsState()
    val dialogs by appViewModel.dialogs.collectAsState()
    val status by appViewModel.status.collectAsState()
    val detailItem by appViewModel.detailItem.collectAsState()


    if (tokenIsRequired && appViewModel.settingsRepository!!.curSource == AppConstants.PERCHANCE.lowercase()) {
        WebView(modifier = Modifier
            .size(0.dp)
            .alpha(0f), url = ApiRoutes.AI_EMBED, viewModel = appViewModel)
    }

    AIMuseTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val context = LocalContext.current
            val fade = alertDialogs.isNotEmpty() || dialogs.isNotEmpty() || status != null
            val animatedValue by animateFloatAsState(
                targetValue = if (fade || appViewModel.bottomScreen.value) 1f else 0f,
                label = "content_alpha",
                animationSpec = tween(durationMillis = 600)
            )
            if (!isSystemInDarkTheme()) {
                val view = LocalView.current
                SideEffect {
                    val window = (view.context as ComponentActivity).window
                    WindowCompat.getInsetsController(window, view)
                        .isAppearanceLightStatusBars = animatedValue < 0.5f
                    if (!appViewModel.bottomScreen.value) {
                        WindowCompat.getInsetsController(window, view)
                            .isAppearanceLightNavigationBars = animatedValue < 0.5f
                    }
                }
            }
            Box(modifier = Modifier
//                .blur(5.dp * animatedValue)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = Color.Black.copy(alpha = .7f * animatedValue)
                    )
                }
            ) {
                MainScreen(appViewModel = appViewModel)
            }
            Box(modifier = Modifier
                .alpha(animatedValue)
                .fillMaxSize()
                .imePadding()
                .statusBarsPadding()) {
                val focusManager = LocalFocusManager.current
                AnimatedBoxScreen(
                    modifier = Modifier,
                    isVisible = appViewModel.bottomScreen.value && !fade,
                    onDismiss = {
                        appViewModel.bottomScreen.value = false
                    },
                    content = {
                        InputPromptContent(
                            modifier = Modifier,
                            item = detailItem,
                            onDialog = {
                                appViewModel.addAlertDialog(it)
                            },
                            callClose = {
                                focusManager.clearFocus()
                                appViewModel.bottomScreen.value = false
                            },
                            makeRequest = { request, counts ->
                                appViewModel.getPrompts(context, request, counts)
                                appViewModel.selectDetailItem(null, 400)
                            })
                    }
                )
                alertDialogs.forEach {
                    DialogAlert(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .alpha(animatedValue),
                        onDismissRequest = if (it.cancelable) ({
                            appViewModel.removeAlertDialog(it) }) else null,
                        onNegative = {
                            appViewModel.removeAlertDialog(it)
                        },
                        onPositive = {
                            it.onPositiveClick?.invoke()
                            appViewModel.removeAlertDialog(it)
                        },
                        dialogTitle = it.title,
                        dialogText = it.message,
                        confirmText = it.positive,
                        dismissText = it.negative
                    )
                }
                var time by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(status != null) {
                    if (status != null) {
                        val startTime = System.currentTimeMillis()
                        while (status != null) {
                            val currentTime = System.currentTimeMillis()
                            time = ((currentTime - startTime) / 1000f).roundToTwoDecimals()
                            delay(10)
                        }
                    }
                }
                LaunchedEffect(status != null) {
                    val showAd = Utils.getRandomBetween(0, 10) == 3
                    if (status != null && showAd) delay(1000)
                    if (status != null && showAd) {
                        val intent = Intent(context, AdsActivity::class.java)
                        context.startActivity(intent)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            (context as? ComponentActivity)?.overrideActivityTransition(
                                OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out, Color.Transparent.toArgb())
                        } else {
                            @Suppress("DEPRECATION")
                            (context as? ComponentActivity)?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        }
                    }
                }
                if (status != null) {
                    DialogStatus(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .alpha(animatedValue),
                        status = status!!, time = time) {
                        appViewModel.stopAllPrompt()
                    }
                }
            }
            dialogs.forEach {
                it.content.invoke()
                BackHandler {
                    appViewModel.removeDialog(it)
                }
            }
        }
    }
}

fun Float.roundToTwoDecimals(): Float {
    return round(this * 100) / 100
}

@Preview(showBackground = true)
@Composable
fun PreviewComposeApp() {
    AIMuseTheme {
        ComposeApp()
    }
}