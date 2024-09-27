package ua.tiar.aim.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animatePanBy
import androidx.compose.foundation.gestures.animateZoomBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.NoAdultContent
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.Utils
import ua.tiar.aim.Utils.downloadImageToCache
import ua.tiar.aim.Utils.shareImage
import ua.tiar.aim.data.models.DialogModel
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.ui.components.AsyncImageText
import ua.tiar.aim.ui.components.CircleElevatedBtn
import ua.tiar.aim.ui.components.CircleElevatedIcon
import ua.tiar.aim.ui.components.OutlinedText
import ua.tiar.aim.ui.components.dropShadow
import ua.tiar.aim.ui.components.slideFromBottom
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.viewmodel.AppViewModel
import java.io.File

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun Detailed(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    detail: ImageResponseModel = ImageResponseModel(
        source = AppConstants.ARTBREEDER,
        modelVersion = "sd-1.5-dreamshaper-8",
        status = "web",
        prompt = "birman cat, sapphire eyes, lilac point fur, on temple steps, monk praying",
        negativePrompt = "dog, cow, other",
        width = 768, height = 512),
    appViewModel: AppViewModel = viewModel(),
    onSelect: ((item: ImageResponseModel) -> Unit)? = null
) {
    val selectedItems by appViewModel.selectedItems.collectAsState()
    val similarItems by remember(detail) {
        if (detail.status != "web") {
            appViewModel.similarImageResponses(detail.promptHash)
        } else {
            emptyFlow()
        }
    }.collectAsState(initial = emptyList())
    val similarItemsFiltered = similarItems.filter { it != detail }
    val copyItems by remember(detail) { appViewModel.copyImageResponses(detail.imageId) }.collectAsState(initial = emptyList())
    val copyItemsFiltered = copyItems.filter { it != detail }
    val similarState: LazyListState = rememberLazyListState()
    LaunchedEffect(similarItemsFiltered) {
        similarState.animateScrollToItem(0)
    }
    val filteredNsfw = appViewModel.settingsRepository?.filteredNsfw ?: true
    val ctx = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isNsfw = detail.maybeNsfw && filteredNsfw
    val imageRequest = remember("${detail.id}${detail.imageId}") {
        if (detail.imageId.isNotEmpty() && !isNsfw) {
            Utils.createImageRequest(ctx, detail)
        } else {
            null
        }
    }
    appViewModel.isShowTitleBar.value = true
    Box(modifier = modifier.fillMaxSize()
        .navigationBarsPadding()
        .statusBarsPadding()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            state = rememberLazyListState()
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
            item {
                Box(modifier = Modifier
                    .height(screenHeight / 2.5f)
                    .fillMaxWidth()) {
                    AsyncImageText(
                        modifier = imageModifier
                            .height(screenHeight / 2.5f)
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(16.dp))
                            .aspectRatio(detail.width / detail.height.toFloat()),
                        imageVector = if (isNsfw) Icons.Rounded.NoAdultContent else  Icons.Rounded.BrokenImage,
                        imageRequest = imageRequest,
                        content = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (detail.status != "web" && appViewModel.hasImageDuplicates(detail.imageId)) {
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
                                if (detail.maybeNsfw && !filteredNsfw) {
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
                        if (!isNsfw) {
                            appViewModel.addDialog(DialogModel {
                                TransformBox(
                                    content = {
                                        AsyncImage(
                                            model = imageRequest,
                                            contentDescription = "imgFull",
                                            error = painterResource(id = R.drawable.broken_image),
                                            contentScale = ContentScale.Fit,
                                            modifier = it
                                                .fillMaxSize()
                                        )
                                    }
                                )
                            })
                        }
                    }
                }
            }
            if (detail.status == "web") {
                item {
                    OutlinedText(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Url",
                        text = detail.imageUrl,
                        showIcons = false,
                        editable = false
                    )
                }
            }
            if (detail.initImage.isNotEmpty()) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val imageRequestInit = ImageRequest.Builder(ctx)
                            .data(detail.initImage)
                            .crossfade(true)
                            .build()
                        OutlinedText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            label = stringResource(id = R.string.init_image),
                            text = detail.initImage,
                            maxLines = 1,
                            showIcons = false,
                            editable = false
                        )
                        AsyncImageText(
                            modifier = Modifier
                                .height(60.dp)
                                .offset(0.dp, -1.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .aspectRatio(1f),
                            shape = RoundedCornerShape(8.dp),
                            imageVector = Icons.Rounded.BrokenImage,
                            imageRequest = imageRequestInit,
                        ) {
                            if (!isNsfw) {
                                appViewModel.addDialog(DialogModel {
                                    TransformBox(
                                        content = {
                                            AsyncImage(
                                                model = imageRequestInit,
                                                contentDescription = "imgFull",
                                                error = painterResource(id = R.drawable.broken_image),
                                                contentScale = ContentScale.Fit,
                                                modifier = it
                                                    .fillMaxSize()
                                            )
                                        }
                                    )
                                })
                            }
                        }
                        OutlinedText(
                            modifier = Modifier.width(90.dp),
                            text = detail.strength.toString(),
                            label = stringResource(id = R.string.strength),
                            showIcons = false,
                            editable = false
                        )
                    }
                }
            }
            if (copyItemsFiltered.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(id = R.string.copies_images),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.padding(start = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        state = rememberLazyListState()
                    ) {
                        items(copyItemsFiltered, key = { "detail-${it.id}" }) { response ->
                            val imageRequestSim = remember(response.imageId) {
                                if (response.imageId.isNotEmpty() && (!response.maybeNsfw || !filteredNsfw)) {
                                    Utils.createImageRequest(ctx, response)
                                } else {
                                    null
                                }
                            }
                            AsyncImageText(
                                modifier = Modifier
                                    .height(100.dp)
                                    .aspectRatio(response.width / response.height.toFloat()),
                                isLoading = response.imageId.isEmpty(),
                                isSelected = selectedItems.contains(response),
                                selectedMode = selectedItems.isNotEmpty(),
                                imageVector = if (response.maybeNsfw && filteredNsfw) Icons.Rounded.NoAdultContent else Icons.Rounded.BrokenImage,
                                imageRequest = imageRequestSim,
                                onClicked = {
                                    if (selectedItems.isNotEmpty())
                                        appViewModel.selectItem(response)
                                    else {
                                        onSelect?.invoke(response)
                                        appViewModel.selectDetailItem(response)
                                    }
                                },
                                onLongClick = {
                                    if (selectedItems.isEmpty()) appViewModel.selectItem(response)
                                }
                            )
                        }
                    }
                }
            }
            item {
                OutlinedText(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(id = R.string.prompt),
                    text = detail.getDonePrompt(),
                    showIcons = false,
                    editable = false
                )
            }
            if (detail.getDoneNegativePrompt().isNotEmpty()) {
                item {
                    OutlinedText(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(id = R.string.prompt_negative),
                        text = detail.getDoneNegativePrompt(),
                        showIcons = false,
                        editable = false
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedText(
                        modifier = Modifier.weight(3f),
                        label = stringResource(id = R.string.seed),
                        text = detail.seed.toString(),
                        showIcons = false,
                        editable = false
                    )
                    OutlinedText(
                        modifier = Modifier.weight(2f),
                        label = stringResource(id = R.string.guidance_scale),
                        text = detail.guidanceScale.toString(),
                        showIcons = false,
                        editable = false
                    )
                }
            }
            if (detail.source.equals(AppConstants.ARTBREEDER, true)) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedText(
                            modifier = Modifier.weight(2f),
                            text = detail.loraScale.toString(),
                            label = stringResource(id = R.string.lora_scale),
                            showIcons = false,
                            editable = false
                        )
                        OutlinedText(
                            modifier = Modifier.weight(2f),
                            text = detail.numSteps.toString(),
                            label = stringResource(id = R.string.steps),
                            showIcons = false,
                            editable = false
                        )
                    }
                }
            }
            item {
                FlowRow(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier
                        .dropShadow(RoundedCornerShape(4.dp), offsetY = 1.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                    ) {
                        Text(text = detail.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                    if (detail.source.equals(AppConstants.ARTBREEDER, true)) {
                        Box(
                            modifier = Modifier
                                .dropShadow(RoundedCornerShape(4.dp), offsetY = 1.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = detail.modelVersion,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    Box(modifier = Modifier
                        .dropShadow(RoundedCornerShape(4.dp), offsetY = 1.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                    ) {
                        Text(text = "${detail.width}x${detail.height}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp))
                    }
                }
            }
            if (detail.status != "web") {
                item {
                    Text(
                        text = stringResource(id = R.string.similar),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        modifier = Modifier.padding(start = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        state = similarState
                    ) {
                        item {
                            AsyncImageText(
                                modifier = Modifier
                                    .height(100.dp)
                                    .aspectRatio(detail.width / detail.height.toFloat()),
                                imageVector = Icons.Rounded.Add,
                                onClicked = {
                                    if (selectedItems.isEmpty()) {
                                        val usedScale =
                                            similarItems.map { item -> item.guidanceScale }.toSet()
                                        val scales = generateSequence(5f) { it + 1 }
                                            .takeWhile { it <= 8f }
                                            .filter { it !in usedScale }
                                            .toList()
                                        val request = ImageRequestModel(
                                            prompt = detail.getDonePrompt(),
                                            negativePrompt = detail.getDoneNegativePrompt(),
                                            resolution = "${detail.width}x${detail.height}",
                                            seed = if (scales.isNotEmpty()) detail.seed else -1,
                                            guidanceScale = if (scales.isNotEmpty()) scales.last() else 7f
                                        )
                                        appViewModel.getPrompts(ctx, request, 1)
                                    }
                                }
                            )
                        }
                        items(similarItemsFiltered, key = { "detail-${it.id}" }) { response ->
                            val imageRequestSim = remember(response.imageId) {
                                if (response.imageId.isNotEmpty() && (!response.maybeNsfw || !filteredNsfw)) {
                                    Utils.createImageRequest(ctx, response)
                                } else {
                                    null
                                }
                            }
                            AsyncImageText(
                                modifier = Modifier
                                    .height(100.dp)
                                    .aspectRatio(response.width / response.height.toFloat()),
                                isLoading = response.imageId.isEmpty(),
                                isSelected = selectedItems.contains(response),
                                selectedMode = selectedItems.isNotEmpty(),
                                imageVector = if (response.maybeNsfw && filteredNsfw) Icons.Rounded.NoAdultContent else Icons.Rounded.BrokenImage,
                                imageRequest = imageRequestSim,
                                onClicked = {
                                    if (selectedItems.isNotEmpty())
                                        appViewModel.selectItem(response)
                                    else {
                                        onSelect?.invoke(response)
                                        appViewModel.selectDetailItem(response)
                                    }
                                },
                                onLongClick = {
                                    if (selectedItems.isEmpty()) appViewModel.selectItem(response)
                                }
                            )
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(140.dp)) }
        }

        val item = appViewModel.detailItem.collectAsState().value
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomEnd),
            visible = item != null && selectedItems.isEmpty(),
            enter = slideFromBottom().first,
            exit = slideFromBottom().second,
        ) {
            ActionsDetailed(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = .5f),
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background
                            ),
                        )
                    ),
                detailItem = detail
            )
        }
    }
    BackHandler {
        if (selectedItems.isNotEmpty()) appViewModel.clearSelects()
        else appViewModel.selectDetailItem(null)
    }
}

@ExperimentalFoundationApi
@Composable
fun TransformBox(
    content: @Composable ((modifier: Modifier) -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        if (scale > .3f || zoomChange >= 1f) scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    val m = Modifier
        .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = rotation,
            translationX = offset.x ,
            translationY = offset.y
        )

    Box(
        Modifier
            .transformable(transformableState)
            .pointerInput(Unit) {
//                detectTransformGestures { _, pan, zoom, rotate ->
//                    if (scale > .8f || zoom >= 1f) scale *= zoom
//                }
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            if (scale == 1f) {
                                transformableState.animateZoomBy(4f)
                            } else {
                                launch {
                                    transformableState.animatePanBy(
                                        Offset(
                                            x = -offset.x,
                                            y = -offset.y
                                        )
                                    )
                                }
                                launch {
                                    animate(
                                        initialValue = scale,
                                        targetValue = 1f,
                                        animationSpec = tween(durationMillis = 300)
                                    ) { value, _ ->
                                        scale = value
                                    }
                                }
                                launch {
                                    animate(
                                        initialValue = rotation,
                                        targetValue = 0f,
                                        animationSpec = tween(durationMillis = 300)
                                    ) { value, _ ->
                                        rotation = value
                                    }
                                }
                            }
                        }
                    }
                )
            }
            .fillMaxSize()
    ) {
        content?.invoke(m)
    }
}

@Composable
fun ActionsDetailed(
    modifier: Modifier = Modifier,
    detailItem: ImageResponseModel,
    appViewModel: AppViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val filteredNsfw = appViewModel.settingsRepository?.filteredNsfw ?: true
    val isNsfw = detailItem.maybeNsfw && filteredNsfw
    val file = detailItem.getImageFile(ctx)
    val coroutineScope = rememberCoroutineScope()
    val launcherDownload = rememberLauncherForActivityResult(CreateDocument("image/*")) { uri ->
        if (uri != null) {
            appViewModel.downloadImages(ctx, uri, listOf(detailItem))
        }
    }
    val shareImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == 0 && detailItem.status == "web") {
            File(ctx.cacheDir, file.name).delete()
        }
    }
    val isInvalid = detailItem.status == "" && !file.exists()

    var c = 5
    if (isNsfw) c -= 2
    if (detailItem.status == "web") c -= 1
    val widthItems = LocalConfiguration.current.screenWidthDp.dp / c
    Box(modifier = modifier) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding()
            .align(Alignment.BottomStart)
            .padding(vertical = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            if (!isNsfw && !isInvalid) CircleElevatedBtn(
                modifier = Modifier.width(widthItems),
                height = 44.dp,
                paddingIcon = 12.dp,
                borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                imageVector = Icons.Rounded.Download,
//                text = stringResource(R.string.download),
                contentDescription = "Download"
            ) {
                launcherDownload.launch(file.name)
            }
            CircleElevatedBtn(
                modifier = Modifier.width(widthItems),
                size = 44.dp,
                paddingIcon = 12.dp,
                borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                imageVector = Icons.Rounded.Edit,
//                text = stringResource(R.string.edit),
                contentDescription = "Edit"
            ) {
                appViewModel.bottomScreen.value = true
            }
            if (!isNsfw && !isInvalid) CircleElevatedBtn(
                modifier = Modifier.width(widthItems),
                height = 44.dp,
                paddingIcon = 12.dp,
                borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                imageVector = Icons.Rounded.Share,
//                text = stringResource(R.string.share),
                contentDescription = "Share"
            ) {
                coroutineScope.launch {
                    val f = if (detailItem.status == "web") detailItem.imageUrl.downloadImageToCache(ctx, file.name) else file
                    f?.shareImage(ctx)?.let {
                        shareImageLauncher.launch(Intent.createChooser(it, null))
                    }
                }
            }

            if (detailItem.status != "web") {
                CircleElevatedBtn(
                    modifier = Modifier.width(widthItems),
                    height = 44.dp,
                    paddingIcon = 12.dp,
                    borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    imageVector = Icons.Rounded.Delete,
//                text = stringResource(R.string.delete),
                    contentDescription = "Delete"
                ) {
                    appViewModel.addAlertDialog(
                        Utils.dialogAlertDelete(ctx) {
                            appViewModel.deleteDetailItem(ctx = ctx)
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDetailed() {
    AIMuseTheme {
        Box(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)) {
            Detailed(
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp)
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            )
        }
    }
}