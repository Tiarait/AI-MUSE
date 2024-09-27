package ua.tiar.aim.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.colorTopBar
import ua.tiar.aim.viewmodel.AppViewModel

@Composable
fun TopBarBox(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    buttons: (@Composable () -> Unit)? = null,
    appViewModel: AppViewModel = viewModel()
) {
    val selectedItems by appViewModel.selectedItems.collectAsState()
    val detailItem by appViewModel.detailItem.collectAsState()
    val isSettingsScreen by appViewModel.settingsScreen.collectAsState()

    val isVisible = appViewModel.isShowTitleBar.value || selectedItems.isNotEmpty() || detailItem != null
    val animatedValue by animateFloatAsState(
        targetValue = if (isVisible) 0f else 1f, label = "PageIndicator_animated",
        animationSpec = tween(durationMillis = 600)
    )
    val animatedValueBack by animateFloatAsState(
        targetValue = if (onBack != null) 1f else 0f, label = "animatedValueBack",
        animationSpec = tween(durationMillis = 200)
    )
    val yOffset = (-36).dp * animatedValue
    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxWidth()
            .offset(y = yOffset)
            .clickable(interactionSource = null, indication = null) {}
    ) {

//        val isDark = isSystemInDarkTheme()
//        val view = LocalView.current
//        SideEffect {
//            if (!isDark) {
//                val window = (ctx as ComponentActivity).window
//                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
//                    appViewModel.isShowTitleBar.value
//            }
//        }
        Box(modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(AppConstants.barRounded),
                blur = 8.dp,
                offsetY = 0.dp
            )
            .background(
                MaterialTheme.colorScheme.colorTopBar,
                RoundedCornerShape(AppConstants.barRounded)
            )
        ) {
            Box(
                modifier = Modifier
                    .alpha(1f - animatedValue)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val text = if (selectedItems.isNotEmpty())
                    selectedItems.size.toString()
                else if (detailItem != null)
                    stringResource(R.string.app_name)
                else if (isSettingsScreen)
                    stringResource(R.string.settings)
                else if (appViewModel.titleBarValue.value == "")
                    stringResource(R.string.app_name)
                else appViewModel.titleBarValue.value
                AnimatedContent(
                    modifier = Modifier
                        .padding(vertical = AppConstants.topBarPadding)
                        .fillMaxWidth(),
                    transitionSpec = {
                        fadeIn(animationSpec = tween(600)) togetherWith fadeOut(
                            animationSpec = tween(
                                600
                            )
                        )
                    },
                    targetState = text.uppercase(),
                    label = "TopBarText"
                ) { targetText ->
                    Text(
                        text = targetText,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )

                }
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.CenterStart),
                    enter = fadeIn(),
                    exit = fadeOut(),
                    visible = onBack != null
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(40.dp),
                        onClick = {
                            onBack?.invoke()
                        }
                    ) {
                        Image(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            contentDescription = "TopBarBack"
                        )
                    }
                }
                Row(modifier = Modifier
                    .padding(start = 34.dp * animatedValueBack)
                    .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.height(56.dp),
                        painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                        contentDescription = "app_icon"
                    )
                    val configuration = LocalConfiguration.current
                    val lang = ConfigurationCompat.getLocales(configuration).get(0) ?: LocaleListCompat.getDefault()[0]!!
                    if (lang.country.lowercase() == "ru" || lang.language.lowercase() == "ru")
                        WavingFlag(
                            modifier = Modifier.height(18.dp)
                        )
                }
                Box(modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd) {
                    buttons?.invoke()
                }
            }
        }
    }
}