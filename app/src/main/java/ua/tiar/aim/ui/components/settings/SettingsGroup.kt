package ua.tiar.aim.ui.components.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.tiar.aim.ui.components.dropShadow
import ua.tiar.aim.ui.theme.colorTopBar

@Composable
fun SettingsGroup(
    @StringRes name: Int? = null,
    content: @Composable ColumnScope.() -> Unit ){
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        if (name != null) Text(stringResource(id = name))
        else Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
                .dropShadow(RoundedCornerShape(12.dp), offsetY = 0.dp)
                .background(MaterialTheme.colorScheme.colorTopBar, RoundedCornerShape(12.dp)),
        ) {
            Column {
                content()
            }
        }
    }
}