package ua.tiar.aim.network.artbreeder.models

import androidx.room.Ignore
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils
import ua.tiar.aim.Utils.toMD5
import ua.tiar.aim.data.models.ImageResponseModel

@Serializable
data class GalleryResponse(
    val key: String? = "",
    val likes: Int? = 0,
    val model: Int? = 0,
    val prompt: String? = null,
    val metadata: JsonElement? = null,
    val image_created_at: String? = null,
    val creator_id: Long? = null,
    val time_string: String? = null,
    val model_name: String? = null,
    val nsfw: Boolean? = null,
    val size: List<Int>? = null
) {
    @Ignore
    fun toImageResponseModel(id: Long = 0L): ImageResponseModel {
        val promptMeta = Utils.regexValJson("prompt", metadata?.toString() ?:"")
        val negativePromptMeta = Utils.regexValJson("negativePrompt", metadata?.toString() ?:"")
        val modelVersion = Utils.regexValJson("model_version", metadata?.toString() ?:"")
        val numSteps = Utils.regexIntJson("num_steps", metadata?.toString() ?:"")
        val seedMeta = Utils.regexIntJson("seed", metadata?.toString() ?:"")
        val guidanceScale = Utils.regexIntJson("guidance_scale", metadata?.toString() ?:"")
        val loraScale = Utils.regexIntJson("lcm_lora_scale", metadata?.toString() ?:"")
        val widthMeta = Utils.regexIntJson("width", metadata?.toString() ?:"")
        val heightMeta = Utils.regexIntJson("height", metadata?.toString() ?:"")
        val initImage = Utils.regexValJson("init_image", metadata?.toString() ?:"")
        val strength = Utils.regexIntJson("strength", metadata?.toString() ?:"")

        val promptText = promptMeta.ifEmpty { prompt ?: "" }
        return ImageResponseModel(
            id = id,
            status = "web",
            source = AppConstants.ARTBREEDER,
            imageId = key ?: "",
            imageUrl = "https://artbreeder.b-cdn.net/imgs/$key.jpeg",
            fileExtension = "jpeg",
            modelVersion = modelVersion,
            numSteps = numSteps.toIntOrNull() ?: 20,
            seed = seedMeta.toLongOrNull() ?: -1L,
            prompt = promptText,
            promptHash = promptText.filter { it.isLetterOrDigit() }.toMD5(),
            width = widthMeta.toIntOrNull() ?: 512,
            height = heightMeta.toIntOrNull() ?: 512,
            guidanceScale = guidanceScale.toFloatOrNull() ?: 1f,
            loraScale = loraScale.toFloatOrNull() ?: 1f,
            negativePrompt = negativePromptMeta,
            initImage = initImage,
            strength = strength.toFloatOrNull() ?: 1f,
            maybeNsfw = nsfw == true
        )
    }
}