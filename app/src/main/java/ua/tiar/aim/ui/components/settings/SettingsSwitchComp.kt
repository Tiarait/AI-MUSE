package ua.tiar.aim.ui.components.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.tiar.aim.R
import ua.tiar.aim.ui.theme.AIMuseTheme

@Composable
fun SettingsSwitchComp(
    modifier: Modifier = Modifier,
    isPro: Boolean = false,
    @DrawableRes iconRes: Int? = null,
    icon: ImageVector? = null,
    @StringRes name: Int,
    state: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = modifier.alpha(if (isPro) .7f else 1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPro) {
                        Icon(
                            painterResource(id = R.drawable.ic_crown_v),
                            contentDescription = name.toString(),
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (iconRes != null) {
                        Icon(
                            painterResource(id = iconRes),
                            contentDescription = name.toString(),
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name.toString(),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(id = name),
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.scale(.8f),
                    checked = state,
                    onCheckedChange = { onClick() }
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(.4f))
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewSettingsSwitchComp() {
    AIMuseTheme {
        SettingsSwitchComp(
            name = R.string.setting_remember_prompt,
            icon = Icons.Rounded.EditNote,
            state = false
        ) {

        }
    }
}