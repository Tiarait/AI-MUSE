package ua.tiar.aim.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.AppConstants
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.colorTopBar

@Composable
fun Bottom(
    modifier: Modifier = Modifier) {
    val orientation = LocalConfiguration.current.orientation
    Box(modifier = modifier
        .padding(horizontal = 8.dp)
        .then(
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            } else {
                Modifier
            }
        )) {
        Box(modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(AppConstants.barRounded, AppConstants.barRounded, 0.dp, 0.dp),
                offsetY = 0.dp,
                blur = 8.dp)
            .background(
                MaterialTheme.colorScheme.colorTopBar,
                RoundedCornerShape(AppConstants.barRounded, AppConstants.barRounded, 0.dp, 0.dp)
            )
            .navigationBarsPadding()
            .fillMaxWidth())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBottom() {
    AIMuseTheme {
        Bottom()
    }
}