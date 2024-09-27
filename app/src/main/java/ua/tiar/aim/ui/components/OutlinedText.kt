package ua.tiar.aim.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.theme.AIMuseTheme


@Composable
fun OutlinedText(
    modifier: Modifier = Modifier,
    text: String = "",
    textMutable: MutableState<String>? = null,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Text
    ),
    shape: Shape = RoundedCornerShape(8.dp),
    label: String = "",
    showIcons: Boolean = true,
    editable: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 99,
    onValueChange: ((String) -> Unit)? = null) {
    val interactionSource = remember { MutableInteractionSource() }
    val textVal = textMutable ?: remember { mutableStateOf(text) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isFocused = rememberSaveable {
        mutableStateOf(false)
    }
    if (isFocused.value && editable) BackHandler {
        focusManager.clearFocus()
    }
    Box(modifier) {
        var textFieldColors = textFieldColors().copy(
            focusedTextColor = if (textVal.value.isEmpty()) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary,
            unfocusedTextColor = if (textVal.value.isEmpty()) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
        )
        if (!editable) {
            textFieldColors = textFieldColors.copy(
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                focusedIndicatorColor = MaterialTheme.colorScheme.outline
            )
            OutlinedTextDefault(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused.value = it.isFocused
                    },
                text = textVal.value,
                label = label,
                minLines = minLines,
                maxLines = maxLines,
                interactionSource = interactionSource,
                textFieldColors = textFieldColors
            )
        } else {
            OutlinedTextField(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused.value = it.isFocused
                    },
                colors = textFieldColors,
                value = textVal.value,
                shape = shape,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = { newValue ->
                    textVal.value = newValue
                    onValueChange?.invoke(newValue)
                },
                placeholder = {
                    Text(placeholder)
                },
                keyboardOptions = keyboardOptions,
                keyboardActions = KeyboardActions(onDone = {
                    onValueChange?.invoke(textVal.value)
                    focusManager.clearFocus()
                }),
                minLines = minLines,
                maxLines = maxLines,
                singleLine = maxLines == 1,
                visualTransformation = if (textVal.value.isEmpty())
                    PlaceholderTransformation(
                        placeholder,
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f))
                else VisualTransformation.None,
                interactionSource = interactionSource,
                label = { if (label.isNotEmpty()) Text(label) },
                trailingIcon = if (showIcons) {
                    {
                        Column {
                            if (textVal.value.isEmpty()) {
                                Icon(
                                    imageVector = Icons.Rounded.Create,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            } else {
                                IconButton(
                                    modifier = Modifier.size(28.dp),
                                    onClick = {
                                        textVal.value = ""
                                        onValueChange?.invoke("")
//                                        focusManager.clearFocus(true)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                } else null
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTextDefault(
    modifier: Modifier = Modifier,
    text: String,
    label: String = "",
    minLines: Int = 1,
    maxLines: Int = 99,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    textFieldColors: TextFieldColors = textFieldColors()
) {
    BasicTextField(
        value = text,
        modifier = modifier,
        singleLine = maxLines == 1,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Color.Red),
        readOnly = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = textFieldColors.unfocusedTextColor
        ),
        onValueChange = {  }
    ) { innerTextField ->
        OutlinedTextFieldDefaults.DecorationBox(
            value = text,
            innerTextField = innerTextField,
            enabled = true,
            singleLine = maxLines == 1,
            interactionSource = interactionSource,
            colors = textFieldColors,
            visualTransformation = VisualTransformation.None,
            label = if (label.isNotEmpty()) {{ Text(label) }} else null,
            container = {
                OutlinedTextFieldDefaults.Container(
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = textFieldColors,
                    shape = RoundedCornerShape(8.dp),
                    focusedBorderThickness = 1.dp,
                    unfocusedBorderThickness = 1.dp
                )
            }
        )
    }
}

@Composable
fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.colors().copy(
        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f),
        disabledTextColor = MaterialTheme.colorScheme.onPrimary,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        focusedLabelColor = MaterialTheme.colorScheme.tertiaryContainer,
        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimary,
        disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .5f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = .3f),
        cursorColor = MaterialTheme.colorScheme.onTertiary,
        textSelectionColors = TextSelectionColors(
            handleColor = MaterialTheme.colorScheme.primary,
            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = .4f))
    )
//    return TextFieldDefaults.colors().copy(
//        focusedTextColor = MaterialTheme.colorScheme.onSurface, // Цвет текста, когда поле в фокусе
//        unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f), // Цвет текста, когда поле не в фокусе
//        focusedIndicatorColor = MaterialTheme.colorScheme.primary, // Цвет индикатора фокуса (например, подчеркивание)
//        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f), // Цвет индикатора, когда поле не в фокусе
//        disabledIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f), // Цвет индикатора, когда поле отключено
//        focusedContainerColor = MaterialTheme.colorScheme.surface, // Цвет фона поля
//        unfocusedContainerColor = MaterialTheme.colorScheme.surface, // Цвет фона поля
//        cursorColor = MaterialTheme.colorScheme.primary, // Цвет курсора
//        textSelectionColors = TextSelectionColors(
//            handleColor = MaterialTheme.colorScheme.primary, // Цвет маркеров выделения
//            backgroundColor = MaterialTheme.colorScheme.secondary // Цвет фона выделения текста
//        )
//    )
}

@Preview(showBackground = false)
@Composable
fun PreviewOutlinedText() {
    AIMuseTheme {
        OutlinedText(text = "OutlinedText", label = "OutlinedText", editable = false)
    }
}