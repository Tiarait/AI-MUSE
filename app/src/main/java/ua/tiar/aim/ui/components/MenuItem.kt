package ua.tiar.aim.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@Composable
fun MenuItem(
    text: String,
    @DrawableRes drawable: Int? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit) {
    androidx.compose.material3.DropdownMenuItem(
        modifier = Modifier.alpha(if (enabled) 1f else .4f),
        leadingIcon = {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Dropdown" + text.replace(" ", ""),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else if (drawable != null) {
                Icon(
                    painter = painterResource(id = drawable),
                    contentDescription = "Dropdown" + text.replace(" ", ""),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        onClick = { onClick.invoke() }
    )
}