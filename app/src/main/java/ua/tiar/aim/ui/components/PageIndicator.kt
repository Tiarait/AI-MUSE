package ua.tiar.aim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun PageIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState? = null) {

    val pageCount = pagerState?.pageCount ?: 2
    val indicatorWidth = (1f / pageCount) * 0.5f
    val indicatorHeight = 8.dp
    val indicatorSpacing = 2.dp

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
//                val alpha = if (pagerState?.currentPage == iteration) .7f else 0.3f
                val alpha = 1f
                Box(
                    modifier = Modifier
                        .padding(indicatorSpacing)
                        .alpha(alpha)
                        .background(
                            if (pagerState?.currentPage == iteration) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(4.dp)
                        )
                        .size(
                            width = (LocalConfiguration.current.screenWidthDp.dp * indicatorWidth) - indicatorSpacing,
                            height = indicatorHeight
                        )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPageIndicator() {
    AIMuseTheme {
        PageIndicator()
    }
}