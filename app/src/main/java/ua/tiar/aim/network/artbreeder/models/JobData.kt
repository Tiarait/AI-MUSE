package ua.tiar.aim.network.artbreeder.models

import kotlinx.serialization.Serializable

@Serializable
sealed class JobData {
    @Serializable
    data class JobDataComposer(
        val seed: Long,
        val prompt: String,
        val guidance_scale: Int = 1,
        val width: Int,
        val height: Int,
        val num_inference_steps: Int = 4,
        val init_image: String? = null,
        val init_image_strength: Float = 0.2f,
        val scribble_guidance_scale: Int = 0,
        val scribble_guidance_image: String? = null,
        val model_name: String = "sdxl-lightning",
        val return_binary: Boolean = false,
        val image_format: String = "jpeg",
        val ipa_data: List<String> = emptyList(),
        val negative_prompt: String,
        val do_upres: Boolean = false,
        val do_upscale: Boolean = false
    ): JobData()

    @Serializable
    data class JobDataPrompter(
        val model_version: String = "sd-1.5-dreamshaper-8",//sd-1.5-realistic//sdxl-1.0-lcm-base//sd-1.5-dreamshaper-8
        val lcm_lora_scale: Float = 1f,
        val guidance_scale: Float = 7f,
        val strength: Float = 1f,
        val prompt: String,
        val negative_prompt: String,
        val seed: Long,
        val width: Int,
        val height: Int,
        val num_steps: Int = 30,
        val crop_init_image: Boolean = true,
        val init_image: String? = null,
    ): JobData()
}