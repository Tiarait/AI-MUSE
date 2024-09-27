package ua.tiar.aim.data.models

import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
data class DialogModel(
    val content: (@Composable () -> Unit)
)
