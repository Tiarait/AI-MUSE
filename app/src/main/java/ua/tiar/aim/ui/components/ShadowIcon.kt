package ua.tiar.aim.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShadowIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    tint: Color = MaterialTheme.colorScheme.onPrimary,
    contentDescription: String? = null,
    blur: Dp = 3.dp
) {
    Box(modifier) {
        Icon(
            modifier = Modifier.fillMaxSize()
                .padding(top = 1.dp, start = 1.dp)
                .offset(x = 1.dp, y = 1.dp)
                .blur(blur),
            imageVector = imageVector,
            tint = Color.Black,
            contentDescription = contentDescription + "Shadow"
        )
        Icon(
            modifier = Modifier.fillMaxSize(),
            imageVector = imageVector,
            tint = tint,
            contentDescription = contentDescription
        )
    }
}