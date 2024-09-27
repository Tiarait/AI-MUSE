package ua.tiar.aim.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DialogAlertModel(
    val title: String = "",
    val message: String = "",
    val positive: String = "",
    val negative: String = "",
    val cancelable: Boolean = true,
    val onPositiveClick: (() -> Unit)? = null,
)
