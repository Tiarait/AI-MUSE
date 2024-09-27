package ua.tiar.aim.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.tiar.aim.AppConstants
import ua.tiar.aim.ui.components.CircleElevatedBtn
import ua.tiar.aim.ui.components.VerticalStaggeredGridImages
import ua.tiar.aim.ui.components.slideFromBottom
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.viewmodel.AppViewModel

@Composable
fun GalleryExternalScreen(
    appViewModel: AppViewModel = viewModel(),
    isVisible: Boolean = true
) {
    val scrollState: LazyStaggeredGridState = rememberLazyStaggeredGridState()
    val imageResponses by appViewModel.galleryResponses.collectAsState()
    val detailItem by appViewModel.detailItem.collectAsState()

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val columns = (screenWidth/AppConstants.minItemWidth).toInt()
    LaunchedEffect(isVisible, imageResponses.isEmpty()) {
        if (imageResponses.isEmpty() && isVisible) {
            appViewModel.getGallery(true)
        }
        if (!isVisible) {
            scrollState.stopScroll()
        }
    }
    LaunchedEffect(scrollState.firstVisibleItemIndex, scrollState.lastScrolledForward, scrollState.lastScrolledBackward) {
        if (isVisible) {
            if (scrollState.firstVisibleItemIndex > columns * 3) {
                if (scrollState.lastScrolledForward && !scrollState.lastScrolledBackward) {
//                    delay(150)
                    appViewModel.isShowTitleBar.value = false
                } else if (!scrollState.lastScrolledForward && scrollState.lastScrolledBackward) {
                    appViewModel.isShowTitleBar.value = true
                }
            } else {
                appViewModel.isShowTitleBar.value = true
            }
        }
    }
    Box(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 0.dp)
        .fillMaxSize()) {
        VerticalStaggeredGridImages(
            modifier = Modifier
                .fillMaxSize(),
            items = imageResponses,
            visible = isVisible,
            viewModel = appViewModel,
            scrollState = scrollState,
            isLoading = appViewModel.isExternalGalleryLoading.value,
            onRefresh = {
                appViewModel.getGallery(true)
            },
            onLoad = {
                appViewModel.getGallery(skip = imageResponses.size)
            }
        )

        val selectedItems by appViewModel.selectedItems.collectAsState()
        val visibleUpButton = columns * 5
        val arrowVisible = scrollState.firstVisibleItemIndex > visibleUpButton &&
                scrollState.lastScrolledBackward &&
                detailItem == null &&
                selectedItems.isEmpty()
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            visible = arrowVisible,
            enter = slideFromBottom(useShrink = true, startDelay = 200).first,
            exit = slideFromBottom(useShrink = true, endDelay = 200).second,
        ) {
            CircleElevatedBtn(
                modifierContainer = Modifier
                    .padding(bottom = 28.dp, end = 16.dp)
                    .navigationBarsPadding()
                    .statusBarsPadding(),
                size = 56.dp,
                paddingIcon = 8.dp,
                imageVector = Icons.Rounded.ArrowUpward,
                borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                backgroundColor = MaterialTheme.colorScheme.background,
                iconColor = MaterialTheme.colorScheme.onBackground
            ) {
                appViewModel.isShowTitleBar.value = true
                scrollState.requestScrollToItem(0)
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewGalleryScreen() {
    AIMuseTheme {
        GalleryExternalScreen()
//        InputPromptContent()
    }
}