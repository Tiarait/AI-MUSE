package ua.tiar.aim.data.models

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable

@Serializable
data class StatusRequestModel(
    @StringRes var action: Int = -1,
    @StringRes var status: Int? = null,
    var reason: String? = null
)
