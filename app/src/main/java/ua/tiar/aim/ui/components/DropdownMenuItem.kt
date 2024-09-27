package ua.tiar.aim.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import ua.tiar.aim.data.models.MenuItem

@Composable
fun DropdownMenuItem(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(8.dp),
    dpOffset: DpOffset = DpOffset(y = 0.dp, x = 0.dp),
    expanded: Boolean = true,
    items: List<MenuItem> = emptyList(),
    onDismiss: (() -> Unit)? = null,
    onClick: ((indexOf: Int) -> Unit)? = null
) {
    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = shape)) {
        DropdownMenu(
            modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
            offset = dpOffset,
            expanded = expanded,
            onDismissRequest = { onDismiss?.invoke() },
            properties = PopupProperties(usePlatformDefaultWidth = false)
        ) {
            items.forEach {
                MenuItem(
                    text = it.title,
                    drawable = it.drawable,
                    icon = it.icon
                ) {
                    onClick?.invoke(items.indexOf(it))
                }
            }
        }
    }
    if (expanded) BackHandler {
        onDismiss?.invoke()
    }
}