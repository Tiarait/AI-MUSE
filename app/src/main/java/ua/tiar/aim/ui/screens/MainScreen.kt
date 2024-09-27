package ua.tiar.aim.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Shader
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.Utils
import ua.tiar.aim.Utils.downloadImageToCache
import ua.tiar.aim.Utils.shareImage
import ua.tiar.aim.data.models.MenuItem
import ua.tiar.aim.ui.components.BackCover
import ua.tiar.aim.ui.components.Bottom
import ua.tiar.aim.ui.components.ButtonGetStarted
import ua.tiar.aim.ui.components.DropdownMenuItem
import ua.tiar.aim.ui.components.GetStartedButton
import ua.tiar.aim.ui.components.PageIndicator
import ua.tiar.aim.ui.components.TopBarBox
import ua.tiar.aim.ui.components.slideFromBottom
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.colorGradientMainCenter
import ua.tiar.aim.viewmodel.AppViewModel
import java.io.File
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    appViewModel: AppViewModel = viewModel()
) {
    val activity = (LocalContext.current as? ComponentActivity)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    LaunchedEffect(pagerState.currentPage) {
        appViewModel.titleBarValue.value = when (pagerState.currentPage) {
            2 -> activity?.getString(R.string.gallery) ?: ""
            3 -> activity?.getString(R.string.about) ?: ""
            else -> activity?.getString(R.string.app_name) ?: ""
        }
        appViewModel.isShowTitleBar.value = pagerState.currentPage != 0
    }
    val isLoading by appViewModel.isPromptLoading
    if (isLoading && pagerState.currentPage != 1) {
        pagerState.requestScrollToPage(1)
    }
    MainContent(
        pagerState = pagerState,
        appViewModel = appViewModel
    )
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        if (appViewModel.settingsScreen.value)
            appViewModel.hideSettings()
        else if (pagerState.currentPage != 0 ) coroutineScope.launch {
            pagerState.animateScrollToPage(0, animationSpec = tween(durationMillis = 200 + 200 * pagerState.currentPage))
        }
        else activity?.finish()
    }

}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun MainContent(
    pagerState: PagerState = rememberPagerState(initialPage = 0, pageCount = { 3 }),
    appViewModel: AppViewModel = viewModel()
) {
    val selectedItems by appViewModel.selectedItems.collectAsState()
    val detailItem by appViewModel.detailItem.collectAsState()
    val bgColor = MaterialTheme.colorScheme.background
    val centerColor = MaterialTheme.colorScheme.colorGradientMainCenter
    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(centerColor, bgColor),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0f, 0.95f)
            )
        }
    }

    val isMainShow by appViewModel.isMainShow.collectAsState()
    val animatedValue by animateFloatAsState(
        targetValue = if (!isMainShow) 0f else 1f, label = "start_animated",
        animationSpec = tween(durationMillis = 1800)
    )
    val isSettingShow by appViewModel.settingsScreen.collectAsState()
    val animatedSettingsValue by animateFloatAsState(
        targetValue = if (isSettingShow) 0f else 1f, label = "settingsScreen_animated",
        animationSpec = tween(durationMillis = 1800)
    )
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.background(largeRadialGradient)) {
        BackCover(modifier = Modifier
            .alpha(1 - pagerState.getOffsetDistanceInPages(0).absoluteValue)
            .blur(5.dp * animatedValue))
        Box(
            modifier = Modifier
                .scale(1f * animatedValue)
                .alpha(1f * animatedValue)
                .fillMaxSize()
        ) {
            val animSettings = slideFromBottom(duration = 800)
            AnimatedVisibility(
                visible = !isSettingShow,
                enter = animSettings.first,
                exit = animSettings.second
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = !appViewModel.bottomScreen.value && selectedItems.isEmpty() && detailItem == null,
                    beyondViewportPageCount = 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) { page ->
                    Box(modifier = Modifier.pagerFadeTransition(page, pagerState = pagerState)) {
                        when (page) {
                            0 -> FirstScreen()
                            1 -> GalleryInternalScreen(
                                appViewModel = appViewModel,
                                isVisible = pagerState.currentPage == 1
                            )

                            2 -> GalleryExternalScreen(
                                appViewModel = appViewModel,
                                isVisible = pagerState.currentPage == 2
                            )

                            3 -> AboutScreen(isVisible = pagerState.currentPage == 3)
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = isSettingShow,
                enter = slideInVertically(tween(800)),
                exit = slideOutVertically(tween(800))
            ) {
                SettingsScreen(appViewModel = appViewModel)
            }
        }
        val pageOffset = pagerState.getOffsetDistanceInPages(pagerState.currentPage)
        val distance = pagerState.getOffsetDistanceInPages(0).absoluteValue
        val alpha = when {
            pagerState.currentPage == 0 -> if (pageOffset <= 0) distance else if (pageOffset <= 1) 1 - distance else 0f
            pagerState.currentPage == 1 && pageOffset > 0 -> distance
            else -> 1f
        }

        TopBarBox(
            modifier = Modifier
                .alpha(alpha)
                .offset(y = (-60).dp * (1 - alpha))
                .navigationBarsPadding(),
            appViewModel = appViewModel,
            onBack = if (isSettingShow || selectedItems.isNotEmpty() || detailItem != null) ({
                if (isSettingShow) {
                    appViewModel.hideSettings()
                } else if (selectedItems.isNotEmpty()) {
                    appViewModel.clearSelects()
                } else if (detailItem != null) {
                    appViewModel.selectDetailItem(null)
                }
            }) else null,
            buttons = {
                val size = 40.dp
                Row(
                    modifier = Modifier
                        .padding(vertical = AppConstants.topBarPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    if (detailItem == null && selectedItems.isEmpty()) {
                        if (pagerState.currentPage == 2 && !isSettingShow) {
                            var expandedOrder by remember { mutableStateOf(false) }
                            IconButton(
                                enabled = !expandedOrder,
                                modifier = Modifier.size(size),
                                onClick = {
                                    expandedOrder = true
                                }
                            ) {
                                Image(
                                    imageVector = Icons.AutoMirrored.Rounded.Sort,
                                    colorFilter = ColorFilter.tint(
                                        if (expandedOrder) MaterialTheme.colorScheme.tertiary else
                                            MaterialTheme.colorScheme.onBackground),
                                    contentDescription = "TopBarSort"
                                )
                            }
                            val curOrder by appViewModel.settingsRepository!!.curOrderGalleryFlow.collectAsState()
                            val curSource by appViewModel.settingsRepository!!.curSourceFlow.collectAsState()
                            val items = listOf(
                                MenuItem(
                                    title = if (curSource == AppConstants.PERCHANCE.lowercase()) AppConstants.ORDER_TOP else AppConstants.ORDER_RANDOM,
                                    icon = if (curOrder == AppConstants.ORDER_TOP) Icons.Default.RadioButtonChecked
                                    else Icons.Default.RadioButtonUnchecked),
                                MenuItem(title = AppConstants.ORDER_RECENT,
                                    icon = if (curOrder == AppConstants.ORDER_RECENT) Icons.Default.RadioButtonChecked
                                    else Icons.Default.RadioButtonUnchecked),
                                MenuItem(title = AppConstants.ORDER_TRENDING,
                                    icon = if (curOrder == AppConstants.ORDER_TRENDING) Icons.Default.RadioButtonChecked
                                    else Icons.Default.RadioButtonUnchecked),
                            )
                            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                DropdownMenuItem(
                                    dpOffset = DpOffset(x = 40.dp, y = 32.dp),
                                    expanded = expandedOrder,
                                    items = items,
                                    onDismiss = { expandedOrder = false },
                                ) { indexOf ->
                                    appViewModel.settingsRepository?.curOrderGallery =
                                        items[indexOf].title.replace(AppConstants.ORDER_RANDOM, AppConstants.ORDER_TOP)
                                    appViewModel.updGallery()
                                    expandedOrder = false
                                }
                            }

                        }
                        IconButton(
                            modifier = Modifier.size(size).alpha(animatedSettingsValue),
                            onClick = {
                                appViewModel.showSettings()
                            }
                        ) {
                            Image(
                                modifier = Modifier.rotate(360f * animatedSettingsValue),
                                imageVector = Icons.Rounded.Settings,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                contentDescription = "TopBarSettings"
                            )
                        }
                    }
                    if (detailItem != null && selectedItems.isEmpty() && !isSettingShow) {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(
                            enabled = !expanded,
                            modifier = Modifier.size(size),
                            onClick = { if (!expanded) expanded = true }
                        ) {
                            Image(
                                imageVector = Icons.Rounded.MoreVert,
                                colorFilter = ColorFilter.tint(
                                    if (expanded) MaterialTheme.colorScheme.tertiary else
                                        MaterialTheme.colorScheme.onBackground),
                                contentDescription = "TopBarMore"
                            )
                        }

                        val file = detailItem?.getImageFile(ctx)
                        val launcherDownload =
                            rememberLauncherForActivityResult(CreateDocument("image/*")) { uri ->
                                if (uri != null) {
                                    appViewModel.downloadImages(
                                        ctx,
                                        uri,
                                        listOf(detailItem!!)
                                    )
                                }
                            }
                        val shareImageLauncher = rememberLauncherForActivityResult(
                            ActivityResultContracts.StartActivityForResult()
                        ) { result ->
                            if (result.resultCode == 0 && detailItem?.status == "web") {
                                File(ctx.cacheDir, file?.name ?: "").delete()
                            }
                        }

                        detailItem?.let {
                            val items: List<MenuItem> = Utils.genImageMenu(
                                ctx,
                                it,
                                appViewModel.settingsRepository?.filteredNsfw ?: true
                            )

                            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                DropdownMenuItem(
                                    dpOffset = DpOffset(x = 0.dp, y = 32.dp),
                                    expanded = expanded,
                                    items = items,
                                    onDismiss = { expanded = false },
                                ) { indexOf ->
                                    when (items[indexOf].id) {
                                        R.string.download -> {
                                            launcherDownload.launch(file?.name ?: "")
                                        }

                                        R.string.edit -> {
                                            appViewModel.bottomScreen.value = true
                                        }

                                        R.string.share -> {
                                            coroutineScope.launch {
                                                val f =
                                                    if (detailItem?.status == "web") detailItem?.imageUrl?.downloadImageToCache(
                                                        ctx,
                                                        file?.name ?: ""
                                                    ) else file
                                                f?.shareImage(ctx)?.let { file ->
                                                    shareImageLauncher.launch(
                                                        Intent.createChooser(
                                                            file,
                                                            null
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        R.string.delete -> {
                                            appViewModel.addAlertDialog(
                                                Utils.dialogAlertDelete(ctx) {
                                                    appViewModel.deleteDetailItem(ctx = ctx)
                                                }
                                            )
                                        }
                                    }
                                    expanded = false
                                }
                            }
                        }
                    }
                    if (selectedItems.isNotEmpty() && !isSettingShow) {
                        val fileName = if (selectedItems.size > 1)
                            "${System.currentTimeMillis()}.zip"
                        else "${selectedItems.firstOrNull()?.imageId}.${selectedItems.firstOrNull()?.fileExtension}"
                        val launcherDownload = rememberLauncherForActivityResult(
                            CreateDocument(if (fileName.endsWith(".zip")) "application/x-zip" else "image/*")
                        ) { uri ->
                            uri?.let {
                                appViewModel.downloadImages(ctx, it, selectedItems)
                            }
                        }
                        IconButton(
                            modifier = Modifier.size(size),
                            onClick = { launcherDownload.launch(fileName) }
                        ) {
                            Image(
                                imageVector = Icons.Rounded.Download,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                contentDescription = "TopBarDownload"
                            )
                        }
                        val noWeb = selectedItems.any { it.status != "web" }
                        if (noWeb) {
                            IconButton(
                                modifier = Modifier.size(size),
                                onClick = {
                                    appViewModel.addAlertDialog(
                                        Utils.dialogAlertDelete(ctx) {
                                            appViewModel.deleteSelects(ctx = ctx)
                                        }
                                    )
                                }
                            ) {
                                Image(
                                    imageVector = Icons.Rounded.Delete,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                                    contentDescription = "TopBarDelete"
                                )
                            }
                        }
                    }
                }
            })

        MainStateButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .scale(1f * animatedValue)
                .alpha(1f * animatedValue),
            currentPage = pagerState.currentPage,
            openBottomScreen = appViewModel.bottomScreen,
            appViewModel = appViewModel
        ) {
            coroutineScope.launch {
                if (pagerState.currentPage == 0) pagerState.animateScrollToPage(
                    1,
                    animationSpec = tween(durationMillis = 800)
                )
                else if (pagerState.currentPage == 1) appViewModel.bottomScreen.value = true
            }
        }
        val anim = slideFromBottom(startDelay = 400)
        AnimatedVisibility(
            modifier = Modifier
                .alpha(alpha)
                .offset(y = 48.dp * (1 - alpha))
                .align(Alignment.BottomCenter),
            visible = detailItem == null,
            enter = anim.first,
            exit = anim.second
        ) {
            Bottom(modifier = Modifier.align(Alignment.BottomCenter))
        }
        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .alpha(0.5f),
            visible = pageOffset != 0f,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(400, 400))
        ) {
            PageIndicator(pagerState = pagerState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(8.dp))
        }

    }
}

fun Modifier.pagerFadeTransition(page: Int, pagerState: PagerState) = graphicsLayer {
    cameraDistance = 32f
    // Calculate the absolute offset for the current page from the
    // scroll position.
    val pageOffset = pagerState.getOffsetDistanceInPages(page)
//    val pageOffset = pagerState.calculateCurrentOffsetForPage(page)

    if (pageOffset <= 0) {
        transformOrigin = TransformOrigin(0f, 0f)
    } else if (pageOffset <= 1) {
        transformOrigin = TransformOrigin(1f, 0f)
    }
    alpha = 1 - pageOffset.absoluteValue
}

@Composable
fun MainStateButton(
    modifier: Modifier,
    currentPage: Int = 0,
    openBottomScreen: MutableState<Boolean> = mutableStateOf(false),
    appViewModel: AppViewModel = viewModel(),
    onClicked: (() -> Unit)? = null
) {
    val selectedItems by appViewModel.selectedItems.collectAsState()
    val detailItem by appViewModel.detailItem.collectAsState()
    val settingsScreen by appViewModel.settingsScreen.collectAsState()
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        ButtonGetStarted(
            visible = currentPage < 2 &&
                    !openBottomScreen.value &&
                    selectedItems.isEmpty() &&
                    detailItem == null &&
                    !settingsScreen,
            content = {
                Box(modifier = Modifier) {
                    GetStartedButton(
                        text = if (currentPage == 0)
                            stringResource(id = R.string.start)
                        else stringResource(id = R.string.create_image),
                        visible = currentPage == 0
                    )
                }
            }
        ) {
            onClicked?.invoke()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    AIMuseTheme {
        MainScreen()
    }
}