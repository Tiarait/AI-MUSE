package ua.tiar.aim.data.models

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable  @Immutable
data class ImageRequestModel(
    var id: Long = 0L,
    val prompt: String = "",
    val negativePrompt: String = "",
    val modelVersion: String = "",
    val source: String = "",
    val resolution: String = "512x768",//512x768//512x512//768x512
    val seed: Long = -1,
    val guidanceScale: Float = 7f,
    val initImage: String = "",
    val strength: Float = 1f,
    val loraScale: Float = 1f,
    val numSteps: Int = 20,
    val style: String = "",
    var status: String = "",
)
