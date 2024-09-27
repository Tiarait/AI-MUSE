package ua.tiar.aim.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.colorOnView
import ua.tiar.aim.ui.theme.gradientText

@Composable
fun DialogAlert(
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onPositive: (() -> Unit)? = null,
    confirmText: String = "",
    dismissText: String = "",
    dialogTitle: String?,
    dialogText: String?,
    icon: ImageVector? = null,
) {
    val titleFun: @Composable (() -> Unit) = {
        if (dialogTitle != null) {
            Box {
                Text(modifier = Modifier, text = dialogTitle, style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black,
                    drawStyle = Stroke(
                        width = 3f,
                        join = StrokeJoin.Round
                    )
                ))
                Text(modifier = Modifier, text = dialogTitle, style = MaterialTheme.typography.titleMedium.copy(
                    brush = MaterialTheme.colorScheme.gradientText
                ))
            }
        }
    }
    val messageFun: @Composable (() -> Unit) = {
        if (dialogText != null) {
            SelectionContainer {
                Text(text = dialogText, style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .8f))
            }
        }
    }
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = onDismissRequest != null || dismissText.isNotEmpty(),
            dismissOnClickOutside = onDismissRequest != null || dismissText.isNotEmpty()),
        onDismissRequest = { onDismissRequest?.invoke() }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(bottom = 16.dp, end = 16.dp, start = 16.dp)
            .clickable(
                interactionSource = null,
                indication = null
            ) { onDismissRequest?.invoke() },
            contentAlignment = Alignment.Center) {
            val w = LocalConfiguration.current.screenWidthDp.dp - 64.dp
            val h = LocalConfiguration.current.screenHeightDp.dp - 64.dp
            Box(
                modifier = modifier
                    .widthIn(min = if (w > h) h else w, max = if (w > h) w else h)
                    .dropShadow(
                        RoundedCornerShape(24.dp),
                        offsetY = 1.dp,
                        spread = 2.dp,
                        blur = 8.dp
                    )
                    .background(MaterialTheme.colorScheme.colorOnView, RoundedCornerShape(24.dp)),
            ) {
                val focusManager = LocalFocusManager.current
                Column(
                    Modifier
                        .widthIn(min = if (w > h) h else w, max = if (w > h) w else h)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            focusManager.clearFocus(true)
                        }) {
                    Spacer(modifier = Modifier.height(24.dp))
                    if (dialogTitle != null) {
                        Row(Modifier.padding(horizontal = 24.dp)) {
                            AnimatedAppIcon(modifier = Modifier.size(32.dp),
                                imageVector = icon,
                                contentDescription = "alert_dialog_app")
                            Spacer(modifier = Modifier.width(8.dp))
                            titleFun.invoke()
                        }

                    }
                    if (dialogTitle != null && dialogText != null)
                        Spacer(modifier = Modifier.height(16.dp))
                    if (dialogText != null) {
                        Column(modifier = Modifier
                            .weight(1f, false)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(rememberScrollState())) {
                            messageFun.invoke()
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                    if (confirmText.isNotEmpty() || dismissText.isNotEmpty()) {
                        Row(modifier = Modifier
                            .align(Alignment.End)
                            .padding(48.dp, 4.dp, 24.dp, 24.dp)) {
                            if (dismissText.isNotEmpty())
                                SimpleOutlinedButton(modifier = Modifier, text = dismissText,
                                    onClicked = {
                                        (onNegative ?: onDismissRequest)?.invoke()
                                    })
                            if (confirmText.isNotEmpty() || dismissText.isNotEmpty()) Spacer(
                                modifier = Modifier.width(8.dp)
                            )
                            if (confirmText.isNotEmpty()) {
                                SimpleOutlinedButton(modifier = Modifier,
                                    text = if (confirmText.lowercase() == "ok") stringResource(id = R.string.ok) else confirmText,
                                    onClicked = {
                                        (onPositive ?: onDismissRequest)?.invoke()
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAlertDialogMin() {
    AIMuseTheme {
        Column {
            DialogAlert(modifier = Modifier,
                dialogTitle = "Dialog Title",
                dialogText = "Message text dialog", confirmText = "Ok")
//            DialogAlert1(dialogTitle = "Dialog Title",
//                dialogText = "Message text dialog", confirmText = "Ok", dismissText = "Cancel")
        }
    }
}