package ua.tiar.aim.data.serializers

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils.toMD5
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.network.perchance.ApiRoutes

object ImageResponseModelSerializer : JsonTransformingSerializer<ImageResponseModel>(ImageResponseModel.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val jsonObject = element.jsonObject
        val prompt = jsonObject["prompt"]?.jsonPrimitive?.content.orEmpty()
        val promptHash = prompt.filter { it.isLetterOrDigit() }.toMD5()

        val imageId = jsonObject["imageId"]?.jsonPrimitive?.content.orEmpty()
        val fileExtension = jsonObject["fileExtension"]?.jsonPrimitive?.content.orEmpty()
        val imageUrl = "${ApiRoutes.AI_IMAGE}/${imageId}.${fileExtension}"
        return buildJsonObject {
            jsonObject.forEach { (key, value) ->
                put(key, value)
            }
            put("promptHash", JsonPrimitive(promptHash))
            put("imageUrl", JsonPrimitive(imageUrl))
            put("source", JsonPrimitive(AppConstants.PERCHANCE.lowercase()))
        }
    }
}