package ua.tiar.aim.data.models

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class MenuItem(
    val id: Int = 0,
    val title: String,
    val enabled: Boolean = true,
    @Contextual val icon: ImageVector? = null,
    @DrawableRes val drawable: Int? = null,
)