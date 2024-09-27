package ua.tiar.aim.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.tiar.aim.R
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.ui.components.VerticalStaggeredGridImages
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.viewmodel.AppViewModel

@Composable
fun GalleryInternalScreen(
    appViewModel: AppViewModel = viewModel(),
    isVisible: Boolean = true,
) {
    val imageResponses by appViewModel.imageResponses.collectAsState()
    Box(modifier = Modifier
        .padding(16.dp, 8.dp, 16.dp, 0.dp)
        .fillMaxSize()) {
        if (imageResponses.isEmpty()) {
            Text(
                text = stringResource(id = R.string.empty_images),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .alpha(.7f)
                    .align(Alignment.Center)
            )
        } else {
            val scrollState = rememberLazyStaggeredGridState()
            if (appViewModel.isPromptLoading.value) LaunchedEffect(Unit) {
                scrollState.scrollToItem(0)
            }
            var isInternalGalleryLoading by remember { mutableStateOf(false) }
            var endedList: List<ImageResponseModel> =
//                    if (appViewModel.settingsRepository?.filteredNsfw == true) {
//                        imageResponses.filterNot { it.maybeNsfw }
//                    } else {
                imageResponses
//                    }
            val updScope = rememberCoroutineScope()
            VerticalStaggeredGridImages(
                modifier = Modifier
                    .fillMaxSize(),
                items = endedList,
                visible = isVisible,
                viewModel = appViewModel,
                scrollState = scrollState,
                isLoading = isInternalGalleryLoading,
                onRefresh = {
                    updScope.launch {
                        isInternalGalleryLoading = true
                        endedList = if (appViewModel.settingsRepository?.filteredNsfw == true) {
                            imageResponses.filterNot { it.maybeNsfw }
                        } else {
                            imageResponses
                        }
                        delay(500)
                        isInternalGalleryLoading = false
                    }
                })
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewSecondScreen() {
    AIMuseTheme {
        GalleryInternalScreen()
//        InputPromptContent()
    }
}