package ua.tiar.aim.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.data.models.DialogAlertModel
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.data.models.MenuItem
import ua.tiar.aim.settings.AppSettings
import ua.tiar.aim.ui.components.AspectBtn
import ua.tiar.aim.ui.components.DropdownMenuItem
import ua.tiar.aim.ui.components.OutlineBtnIcon
import ua.tiar.aim.ui.components.OutlinedText
import ua.tiar.aim.ui.components.SimpleOutlinedButton
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.gradientText

@Composable
fun InputPromptContent(
    modifier: Modifier = Modifier,
    item: ImageResponseModel? = null,
    makeRequest: ((request: ImageRequestModel, count: Int) -> Unit)? = null,
    onDialog: ((dialog: DialogAlertModel) -> Unit)? = null,
    callClose: (() -> Unit)? = null
) {
    val appSettings = AppSettings(LocalContext.current)
    if (!appSettings.isNeedSaveLast) appSettings.removeLastVal()
    val source = rememberSaveable { mutableStateOf(appSettings.curSource) }//TODO item?.source ?: appSettings.curSource
//    val source = rememberSaveable { mutableStateOf(item?.source ?: appSettings.curSource) }
    val textPrompt = rememberSaveable { mutableStateOf(item?.getDonePrompt() ?: appSettings.getLastPromptVal()) }
    val textNegativePrompt = rememberSaveable { mutableStateOf(item?.getDoneNegativePrompt() ?: appSettings.getLastNegativePromptVal()) }
    val seed = rememberSaveable { mutableStateOf(item?.seed?.toString() ?: appSettings.getLastSeedVal()) }
    val guidanceScale = rememberSaveable { mutableStateOf(item?.guidanceScale?.toString() ?: appSettings.getLastGuidanceScaleVal()) }
    val steps = rememberSaveable { mutableStateOf(item?.numSteps?.toString() ?: appSettings.getLastStepsVal()) }
    val loraScale = rememberSaveable { mutableStateOf(item?.loraScale?.toString() ?: appSettings.getLastLoraScaleVal()) }
    val count = rememberSaveable { mutableIntStateOf(if (item != null) { 1 } else appSettings.getLastCountVal()) }
    val resolution = rememberSaveable { mutableStateOf(if (item != null) { "${item.width}x${item.height}" } else appSettings.getLastResolutionVal()) }
    val modelVersion = rememberSaveable { mutableStateOf(item?.modelVersion ?: appSettings.getLastModelVersionVal()) }
    val initImage = rememberSaveable { mutableStateOf(item?.initImage ?: appSettings.getLastInitImageVal()) }
    val strength = rememberSaveable { mutableStateOf(item?.strength?.toString() ?: appSettings.getLastStrengthVal()) }
    val isExtends = rememberSaveable { mutableStateOf( appSettings.getExtendEditVal() ) }

    val seedVal = seed.value.toIntOrNull() ?: -1
    if (seedVal == -1) seed.value = ""

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp)
        ) {
            Header(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                callClose)
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedContent(targetState = isExtends.value,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300, easing = EaseIn)) togetherWith
                            fadeOut(animationSpec = tween(300, easing = EaseIn)) using
                            SizeTransform(clip = false,
                                sizeAnimationSpec = { initialSize, targetSize ->
                                    tween(300, easing = EaseInOut)
                                })
                }, label = "inputPromptContentAnim"
            ) { isExtend ->
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        InputField(
                            value = textPrompt,
                            label = stringResource(id = R.string.prompt),
                            minLines = 2,
                            placeholder = "birman cat, sapphire eyes, lilac point fur, on temple steps, monk praying"
                        )
                    }
                    if (isExtend) {
                        item {
                            InputField(
                                modifier = Modifier,
                                value = textNegativePrompt,
                                label = stringResource(id = R.string.prompt_negative),
                                placeholder = stringResource(id = R.string.prompt_negative_text)
                            )
                        }
                    }
                    if (isExtend) {
                        if (source.value.equals(AppConstants.ARTBREEDER, true)) {
                            item {
                                ModelVersionSelector(
                                    modifier = Modifier,
                                    modelVersion = modelVersion
                                )
                            }
                        }
                    }
                    item {
                        SizesSelector(resolution, source, onDialog)
                    }
//                item {
//                    Text("Styles", style = MaterialTheme.typography.labelSmall,
//                        color = MaterialTheme.colorScheme.onPrimary,
//                        modifier = Modifier.padding(horizontal = 4.dp))
//                    Spacer(modifier = Modifier.height(8.dp))
//                    StyleSelector()
//                }
                    if (isExtend) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IntInput(
                                    modifier = Modifier.weight(3f),
                                    text = seed,
                                    placeholder = "-1 (${stringResource(id = R.string.random)})",
                                    label = stringResource(id = R.string.seed)
                                )
                                IntInput(
                                    modifier = Modifier.weight(2f),
                                    text = guidanceScale,
                                    placeholder = "7",
                                    label = stringResource(id = R.string.guidance_scale)
                                )
                            }
                        }
                        if (source.value == "artbreeder") item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IntInput(
                                    modifier = Modifier.weight(2f),
                                    text = loraScale,
                                    placeholder = "1.0",
                                    label = stringResource(id = R.string.lora_scale)
                                )
                                IntInput(
                                    modifier = Modifier.weight(2f),
                                    text = steps,
                                    placeholder = if ((guidanceScale.value.toFloatOrNull()
                                            ?: 7f) >= 2f
                                    ) "20" else "5",
                                    label = stringResource(id = R.string.steps)
                                )
                            }
                        }
                        if (source.value == "artbreeder") item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InputField(
                                    modifier = Modifier.weight(1f),
                                    value = initImage,
                                    placeholder = "url",
                                    maxLines = 1,
                                    label = stringResource(id = R.string.init_image)
                                )
                                IntInput(
                                    modifier = Modifier.width(90.dp),
                                    text = strength,
                                    label = stringResource(id = R.string.strength),
                                    showIcons = false,
                                    placeholder = (.85f).toString()
                                )
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        ActionButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            textPrompt = textPrompt,
            textNegativePrompt = textNegativePrompt,
            resolution = resolution,
            seed = seed,
            guidanceScale = guidanceScale,
            modelVersion = modelVersion,
            initImage = initImage,
            strength = strength,
            steps = steps,
            loraScale = loraScale,
            count = count,
            isExtends = isExtends,
            onDialog = onDialog,
            callClose = callClose,
            makeRequest = makeRequest
        )
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, callClose: (() -> Unit)? = null) {
    Box(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.create_image),
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color.Black,
                drawStyle = Stroke(
                    width = 2f,
                    join = StrokeJoin.Round
                )
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        Text(
            text = stringResource(id = R.string.create_image),
            style = MaterialTheme.typography.labelLarge.copy(
                brush = MaterialTheme.colorScheme.gradientText
            ),
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterEnd),
            onClick = {
                callClose?.invoke()
            }
        ) {
            Image(
                imageVector = Icons.Rounded.ExpandMore,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                contentDescription = "hide"
            )
        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    value: MutableState<String>,
    label: String,
    minLines: Int = 1,
    maxLines: Int = 99,
    placeholder: String
) {
    OutlinedText(
        modifier = modifier.fillMaxWidth(),
        textMutable = value,
        label = label,
        placeholder = placeholder,
        minLines = minLines,
        maxLines = maxLines
    )
}

@Composable
fun IntInput(modifier: Modifier = Modifier,
             text: MutableState<String>,
             placeholder: String = "",
             showIcons: Boolean = true,
             label: String = "") {
    OutlinedText(
        modifier = modifier,
        label = label,
        textMutable = text,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            capitalization = KeyboardCapitalization.None,
            imeAction = ImeAction.Done
        ),
        showIcons = showIcons,
        onValueChange = { text.value = it }
    )
}


@Composable
fun ModelVersionSelector(
    modifier: Modifier = Modifier,
    modelVersion: MutableState<String>) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.model),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        val list = listOf(
            "SD1.5 dreamshaper-8" to "sd-1.5-dreamshaper-8",
            "SD1.5 Realistic" to "sd-1.5-realistic",
            "SDXL LCM Base" to "sdxl-1.0-lcm-base",
            "SDXL Lightning" to "sdxl-lightning"
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = list) { item ->
                SimpleOutlinedButton(
                    modifier = Modifier.alpha(if (modelVersion.value == item.second) 1f else .7f),
                    text = item.first,
                    fontWeight = FontWeight.Medium,
                    fontStyle = MaterialTheme.typography.labelSmall,
                    padding = 8.dp,
                    elevation = 0.dp,
                    shape = RoundedCornerShape(4.dp),
                    border = if (modelVersion.value == item.second) BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.tertiary
                    ) else null,
                    onClicked = {
                        modelVersion.value = item.second
                    }
                )
            }
        }
    }
}

@Composable
fun SizesSelector(
    resolution: MutableState<String>,
    source: MutableState<String>,
    onDialog: ((dialog: DialogAlertModel) -> Unit)? = null) {
    Text(
        text = stringResource(id = R.string.aspect_ratio),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
    val list = if (source.value.equals(AppConstants.ARTBREEDER, true)) {
        listOf(
            512 to 512,
            1024 to 1024,
            1280 to 1280,
            512 to 768,
            768 to 1024,
            1024 to 1280,
            768 to 512,
            1024 to 768,
            1280 to 1024,
//            1366 to 768,
//            1080 to 1536,//TODO is max
        )
    } else {
        listOf(
            512 to 512,
            512 to 768,
            768 to 512,
        )
    }
    val def =
        resolution.value.split("x").first().toIntOrNull() to
                resolution.value.split("x").last().toIntOrNull()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (source.value.equals(AppConstants.ARTBREEDER, true)) {
            item {
                val noAvailable = stringResource(id = R.string.not_available)
                val noAvailablePro = stringResource(id = R.string.not_available_pro)
                OutlineBtnIcon(
                    modifier = Modifier,
                    size = 72.dp,
                    text = stringResource(id = R.string.custom),
                    drawable = R.drawable.ic_crown_v,
                    iconColor = MaterialTheme.colorScheme.onTertiary,
                    isSelected = !list.contains(def),
                    onClicked = {
                        onDialog?.invoke(
                            DialogAlertModel(
                                title = noAvailable,
                                message = noAvailablePro,
                                positive = "Ok",
                                cancelable = false)
                        )
                    }
                )
            }
        }
        items(items = list) { item ->
            AspectBtn(
                modifier = Modifier,
                size = 72.dp,
                width = item.first,
                height = item.second,
                isSelected = resolution.value == "${item.first}x${item.second}",
                onClicked = { resolution.value = "${item.first}x${item.second}" }
            )
        }
    }
}

@Composable
fun StyleSelector() {
    val selected = rememberSaveable { mutableStateOf("No style") }
    LazyRow(
        modifier = Modifier.padding(start = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(AppConstants.artStyles) { name ->
            OutlineBtnIcon(
                modifier = Modifier,
                size = 72.dp,
                text = name,
                isTextInside = true,
                isSelected = selected.value == name,
                onClicked = { selected.value = name }
            )
        }
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    textPrompt: MutableState<String>,
    textNegativePrompt: MutableState<String>,
    resolution: MutableState<String>,
    seed: MutableState<String>,
    guidanceScale: MutableState<String>,
    loraScale: MutableState<String>,
    modelVersion: MutableState<String>,
    initImage: MutableState<String>,
    strength: MutableState<String>,
    steps: MutableState<String>,
    count: MutableState<Int>,
    isExtends: MutableState<Boolean>,
    onDialog: ((dialog: DialogAlertModel) -> Unit)? = null,
    callClose: (() -> Unit)? = null,
    makeRequest: ((request: ImageRequestModel, count: Int) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val isEnabled = textPrompt.value.isNotEmpty()
        val appSettings = AppSettings(LocalContext.current)

        SimpleOutlinedButton(
            modifier = Modifier.weight(2f),
            text = stringResource(id = if (isExtends.value) R.string.less else R.string.more),
            onClicked = {
                isExtends.value = !isExtends.value
                appSettings.setExtendEditVal(isExtends.value)
            }
        )
//        val isChange = item?.let {
//            textPrompt.value != it.getDonePrompt() ||
//                    resolution.value != "${it.width}x${it.height}" ||
//                    seed.value != it.seed.toString() ||
//                    textNegativePrompt.value != it.getDoneNegativePrompt() ||
//                    modelVersion.value != it.modelVersion ||
//                    guidanceScale.value.ifEmpty { "7" } != it.guidanceScale.toString() ||
//                    steps.value.ifEmpty { "20" } != it.numSteps.toString()
//        } ?: run {
//            textPrompt.value.isNotEmpty() ||
//                    resolution.value != "512x768" ||
//                    seed.value.isNotEmpty() ||
//                    textNegativePrompt.value.isNotEmpty() ||
//                    modelVersion.value != "sd-1.5-dreamshaper-8" ||
//                    guidanceScale.value.ifEmpty { "7" } != "7" ||
//                    steps.value.ifEmpty { "20" } != "20"
//        }
//
//        SimpleOutlinedButton(
//            modifier = Modifier,
//            text = stringResource(id = R.string.reset),
//            enabled = isChange,
//            onClicked = {
//                if (item != null) {
//                    textPrompt.value = item.getDonePrompt()
//                    textNegativePrompt.value = item.getDoneNegativePrompt()
//                    resolution.value = "${item.width}x${item.height}"
//                    seed.value = item.seed.toString()
//                    guidanceScale.value = item.guidanceScale.toString()
//                    modelVersion.value = item.modelVersion
//                    steps.value = item.numSteps.toString()
//                    count.value = 1
//                } else {
//                    textPrompt.value = ""
//                    textNegativePrompt.value = ""
//                    resolution.value = "512x768"
//                    modelVersion.value = "sd-1.5-dreamshaper-8"
//                    seed.value = ""
//                    guidanceScale.value = ""
//                    steps.value = ""
//                    count.value = 1
//                    appSettings.resetLast()
//                }
//            }
//        )
        val coroutineScope = rememberCoroutineScope()
        SimpleOutlinedButton(
            modifier = Modifier.weight(4f),
            text = stringResource(id = R.string.generate),
            enabled = isEnabled,
            onClicked = {
                val request = ImageRequestModel(
                    prompt = textPrompt.value,
                    negativePrompt = textNegativePrompt.value,
                    resolution = resolution.value,
                    initImage = initImage.value,
                    loraScale = loraScale.value.toFloatOrNull() ?: 1f,
                    strength = strength.value.toFloatOrNull() ?: .85f,
                    seed = seed.value.toLongOrNull() ?: -1L,
                    numSteps = steps.value.toIntOrNull() ?: if ((guidanceScale.value.toFloatOrNull() ?: 7f) >= 2f) 20 else 5,
                    modelVersion = modelVersion.value,
                    guidanceScale = guidanceScale.value.toFloatOrNull() ?: (if (appSettings.curSource == AppConstants.PERCHANCE.lowercase()) 7f else 1.5f)
                )
                if (appSettings.isNeedSaveLast) appSettings.saveLast(request, count.value)
                coroutineScope.launch {
                    makeRequest?.invoke(request, count.value)
                    delay(400)
                    callClose?.invoke()
                }
            }
        )
        Box {
            var expanded by remember { mutableStateOf(false) }
            val noAvailable = stringResource(id = R.string.not_available)
            val noAvailablePro = stringResource(id = R.string.not_available_pro)
            SimpleOutlinedButton(
                text = count.value.toString(),
                enabled = isEnabled,
            ) {
                expanded = !expanded
            }
            val items = ArrayList<MenuItem>()
            items.apply {
                for (n in 4 downTo 1) {
                    add(MenuItem(title = "${n * 5}", drawable = R.drawable.ic_crown_v, enabled = false))
                }
                for (n in 4 downTo 1) {
                    add(MenuItem(id = n, title = "$n", drawable = R.drawable.ic_empty))
                }
            }
            DropdownMenuItem(
                modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer,
                    RoundedCornerShape(12.dp, 12.dp, 0.dp, 0.dp)),
                expanded = expanded,
                items = items,
                onDismiss = { expanded = false },
            ) { indexOf ->
                if (!items[indexOf].enabled) {
                    onDialog?.invoke(
                        DialogAlertModel(
                            title = noAvailable,
                            message = noAvailablePro,
                            positive = "Ok",
                            cancelable = false)
                    )
                } else {
                    count.value = items[indexOf].id
                    expanded = false
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewInputPromptContent() {
    AIMuseTheme {
        InputPromptContent(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            item = ImageResponseModel(width = 512, height = 768, source = AppConstants.ARTBREEDER)
            )
    }
}