package ua.tiar.aim.ui.components

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.NoAdultContent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.ui.screens.Detailed
import ua.tiar.aim.viewmodel.AppViewModel


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VerticalStaggeredGridImages(
    modifier: Modifier = Modifier,
    items: List<ImageResponseModel>,
    visible: Boolean = true,
    isLoading: Boolean = false,
    viewModel: AppViewModel = viewModel(),
    scrollState: LazyStaggeredGridState,
    onRefresh: (() -> Unit)? = null,
    onLoad: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeightPx = LocalContext.current.resources.displayMetrics.heightPixels
    val columns = (screenWidth/AppConstants.minItemWidth).toInt()
    val grid = StaggeredGridCells.Fixed(columns)//.Adaptive(.dp)

    val selectedItems by viewModel.selectedItems.collectAsState()
    val detailItem by viewModel.detailItem.collectAsState()
    val filteredNsfw = viewModel.settingsRepository?.filteredNsfw ?: true

    val orientation = LocalConfiguration.current.orientation

    var stayVisible by remember { mutableStateOf( false ) }
    SharedTransitionLayout(modifier = modifier.fillMaxSize()
        .then(
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            } else {
                Modifier
            }
        )
//        .clip(RoundedCornerShape(16.dp))
    ) {
        val shapeTransaction = RoundedCornerShape(16.dp)
        AnimatedContent(
            modifier = modifier.fillMaxSize(),
            targetState = detailItem,
            transitionSpec = {
                fadeIn(animationSpec = tween(800)) togetherWith fadeOut(animationSpec = tween(800))
            },
            label = "AnimatedContentDetail"
        ) { detail ->
            if (detail != null) {
                stayVisible = true
                Detailed(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageModifier = Modifier
                        .sharedBoundsModifier(
                            sharedContentState = rememberSharedContentState(key = "${detail.id}${detail.imageId}-bounds"),
                            animatedVisibilityScope = this,
                            sharedScope = this@SharedTransitionLayout
                        ),
                    detail = detail
                ) {
                    scrollState.requestScrollToItem(items.indexOf(it), -(screenHeightPx/4))
                }
            } else {
                if (onLoad != null) LaunchedEffect(scrollState.firstVisibleItemIndex) {
                    val lastVisibleItemIndex = scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    val totalItemCount = scrollState.layoutInfo.totalItemsCount
                    if (items.isNotEmpty() && !isLoading && lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItemCount - (columns + 1)) {
                        onLoad.invoke()
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    val offsetY = remember { mutableFloatStateOf(0f) }
                    val maxOffset = screenHeightPx / 4f
                    val nestedScrollConnection = remember(isLoading) {
                        swipeNestedScrollConnection(scrollState, offsetY, maxOffset, !isLoading) {
                            onRefresh?.invoke()
                        }
                    }
                    val cardOffset by animateFloatAsState(
                        targetValue = offsetY.floatValue, label = "cardOffset"
                    )
                    val cardRotation by animateFloatAsState(
                        targetValue = when {
                            offsetY.floatValue > (maxOffset - (maxOffset/10)) -> 5f
                            offsetY.floatValue > 0f -> 5 * (offsetY.floatValue/maxOffset)
                            else -> 0f
                        }, label = "cardRotation"
                    )
                    val animatedOffset by animateFloatAsState(
                        targetValue = if (isLoading) .5f else 1f, label = "animatedGridAlpha",
                        animationSpec = tween(durationMillis = 400, delayMillis = if (!isLoading) 200 else 0)
                    )
                    val random by remember(isLoading) { mutableIntStateOf(Utils.getRandomBetween(-1, 1)) }
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(animatedOffset)
                            .nestedScroll(nestedScrollConnection),
                        state = scrollState,
                        columns = grid,
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(columns) { Spacer(modifier = Modifier.height(AppConstants.topBarHeight)) }
                        itemsIndexed(
                            items,
                            key = { _, it -> "${it.id}${it.imageId}" }) { ind, response ->
                            var itemVisible by remember { mutableStateOf(stayVisible) }

                            val imageRequest = remember(response.imageId) {
                                if (response.imageId.isNotEmpty() && (!response.maybeNsfw || !filteredNsfw)) {
                                    Utils.createImageRequest(ctx, response)
                                } else null
                            }
                            LaunchedEffect(visible) {
                                if (itemVisible) {
                                    stayVisible = false
                                } else if (visible) {
                                    delay(100)
                                    itemVisible = true
                                }
                            }
                            AnimatedContent(
                                modifier = Modifier
                                    .graphicsLayer {
                                        rotationZ =
                                            cardRotation * if (ind % 2 == 0) random else -random
                                        translationY = cardOffset
                                    }
                                    .animateItem(placementSpec = tween(durationMillis = 400))
                                    .sharedBoundsModifier(
                                        sharedContentState = rememberSharedContentState(key = "${response.id}${response.imageId}-bounds"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        sharedScope = this@SharedTransitionLayout
                                    )
                                    .aspectRatio(response.width / response.height.toFloat()),
                                targetState = itemVisible,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(400)) togetherWith fadeOut(
                                        animationSpec = tween(400)
                                    )
                                }, label = "gridAnimatedContent"
                            ) { isVisible ->
                                if (!isVisible) {
                                    ElevatedCard(modifier = Modifier
                                        .border(BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline), shapeTransaction),
                                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                        shape = shapeTransaction
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize())
                                    }
                                } else {
                                    AsyncImageText(
                                        modifier = Modifier,
                                        imageRequest = imageRequest,
                                        imageVector = if (response.maybeNsfw && filteredNsfw) Icons.Rounded.NoAdultContent else Icons.Rounded.BrokenImage,
                                        isLoading = response.imageId.isEmpty(),
                                        isSelected = selectedItems.contains(response),
                                        selectedMode = selectedItems.isNotEmpty(),
                                        text = response.getDonePrompt(),
                                        shape = shapeTransaction,
                                        onLongClick = {
                                            if (selectedItems.isEmpty()) viewModel.selectItem(
                                                response
                                            )
                                        },
                                        content = {
                                            Row(
                                                modifier = Modifier
                                                    .alpha(if (response.imageId.isEmpty()) 0f else 1f)
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp, horizontal = 4.dp),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                if (response.status != "web" && viewModel.hasImageDuplicates(
                                                        response.imageId
                                                    )
                                                ) {
                                                    CircleElevatedIcon(
                                                        modifierContainer = Modifier
                                                            .alpha(.8f)
                                                            .padding(2.dp),
                                                        imageVector = Icons.Rounded.ContentCopy,
                                                        paddingIcon = 5.dp,
                                                        elevation = 2.dp,
                                                        size = 24.dp
                                                    )
                                                }
                                                if (response.maybeNsfw && !filteredNsfw) {
                                                    CircleElevatedIcon(
                                                        modifierContainer = Modifier
                                                            .alpha(.8f)
                                                            .padding(2.dp),
                                                        imageVector = Icons.Rounded.ErrorOutline,
                                                        iconColor = Color.Red.copy(alpha = 0.6f),
                                                        paddingIcon = 3.dp,
                                                        elevation = 2.dp,
                                                        size = 24.dp
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        if (selectedItems.isNotEmpty())
                                            viewModel.selectItem(response)
                                        else {
                                            viewModel.selectDetailItem(response)
                                        }
                                    }
                                }
                            }
                        }
                        items(columns) { Spacer(modifier = Modifier.height(192.dp)) }
                    }
                    PullToRefreshIndicator(
                        swipeOffset = offsetY.floatValue,
                        isLoading = isLoading,
                        maxOffset = maxOffset
                    )
                }
            }
        }
    }
    if (selectedItems.isNotEmpty()) BackHandler {
        viewModel.clearSelects()
    }
}