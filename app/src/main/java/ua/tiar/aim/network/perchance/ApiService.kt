package ua.tiar.aim.network.perchance

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils
import ua.tiar.aim.Utils.append
import ua.tiar.aim.data.models.ApiResponse
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.data.serializers.ImageResponseModelSerializer
import ua.tiar.aim.repository.ApiRepository
import java.io.FileOutputStream

class ApiService(private val client: HttpClient): ApiRepository {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getGallery(sort: String, timeRange: String, hideIfScoreIsBelow: String, nsfwFiltered: Boolean, channel: String, skip: Int): List<ImageResponseModel> {
        val response: HttpResponse = client.get(ApiRoutes.AI_GALLERY) {
            url {
                parameters.apply {
                    append("sort", sort.lowercase())//trending//recent
                    append("timeRange", timeRange)//all-time
                    append("hideIfScoreIsBelow", hideIfScoreIsBelow)//-1
                    append("contentFilter", if (nsfwFiltered) "pg13" else "none")
                    append("subChannel", "public")
                    append("channel", channel)
                    append("imageElementsHtmlOnly", "true")
                    append("skip", skip.toString())
                }
            }
        }
        val responseBody: String = response.bodyAsText()
        val regex = """(?=<div\s+class="imageCtn"[^>]*>)""".toRegex()
        val list = ArrayList<ImageResponseModel>()
        responseBody.split(regex).forEach {
            val str = it
            var aspectRatio = Utils.regex("""style="aspect-ratio:([^"]*)"""", str) ?: ""
            if (aspectRatio.contains(";")) aspectRatio.split(";").forEachIndexed { _, a ->
                if (a.length == 7 && a.contains("/")) {
                    aspectRatio = a
                    return@forEachIndexed
                }
            }
            if (aspectRatio.isNotEmpty()) {
                val imageId = Utils.regexVal("data-image-id", str)
                val fileExtension = (Utils.regex("""/$imageId.([^"]*)'""", str)
                    ?: Utils.regex("""/$imageId.([^"]*)"""", str)) ?: "jpeg"
                val item = ImageResponseModel(
                    id = list.size.toLong(),
                    status = "web",
                    source = AppConstants.PERCHANCE.lowercase(),
                    imageId = imageId,
                    imageUrl = "${ApiRoutes.AI_IMAGE}/${imageId}.${fileExtension}",
                    fileExtension = fileExtension,
                    prompt = Utils.regexVal("data-prompt", str),
                    negativePrompt = Utils.regexVal("data-negative-prompt", str),
                    seed = Utils.regexVal("data-seed", str).toLongOrNull() ?: -1L,
                    guidanceScale = Utils.regexVal("data-guidance-scale", str).toFloatOrNull() ?: 7f,
                    maybeNsfw = Utils.regexVal("data-is-nsfw", str) == "true",
                    width = (aspectRatio.split("/").firstOrNull() ?: "512").toIntOrNull() ?: 512,
                    height = (aspectRatio.split("/").lastOrNull() ?: "512").toIntOrNull() ?: 512,
                )
                list.add(item)
            }
        }
        return list.toList()
    }

    override suspend fun getPrompt(request: ImageRequestModel, channelName: String, userKey: String, nsfwFiltered: Boolean): ApiResponse {
        val response: HttpResponse = client.get(ApiRoutes.AI_GENERATE) {
            url {
                parameters.apply {
                    append("prompt", request.prompt
                        .append(AppConstants.prompt)
                    )
                    append("negativePrompt", request.negativePrompt
                        .append(AppConstants.negativePrompt)
                    )
                    append("resolution", request.resolution)
                    append("seed", request.seed.toString())
                    append("channel", channelName)
                    append("guidanceScale", request.guidanceScale.toString())
                    append("userKey", userKey)
                }
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.e("wtf", "getPrompt request=$request")
        Log.e("wtf", "getPrompt responseBody=$responseBody")
        return if (responseBody.isNotEmpty()) {
            runCatching { json.decodeFromString(ImageResponseModelSerializer, responseBody) }
                .map { ApiResponse.Success(it) }
                .getOrElse {
                    runCatching { json.decodeFromString<ApiResponse.Failed>(responseBody) }
                        .getOrElse {
                            runCatching { json.decodeFromString<ApiResponse.InvalidType>(responseBody) }
                                .getOrElse {
                                    runCatching { json.decodeFromString<ApiResponse.Invalid>(responseBody) }
                                        .getOrDefault(ApiResponse.Invalid(responseBody))
                                }
                        }
                }
        } else {
            ApiResponse.Failed(reason = "Code: ${response.status.value}")
        }
    }

    override suspend fun getUserVerify(token: String, thread: String): ApiResponse {
        val response: HttpResponse = client.get(ApiRoutes.AI_USER_VERIFY) {
            url {
                parameters.apply {
                    if (token.isNotEmpty())
                        append("token", token)
                    if (thread.isNotEmpty())
                        append("thread", thread)
                }
            }
        }
        val responseBody: String = response.bodyAsText()
        Log.e("wtf", "getUserVerify responseBody=$responseBody token=${token} thread=${thread}")
        return if (responseBody.isNotEmpty()) {
            runCatching { json.decodeFromString<ApiResponse.User>(responseBody) }
                .map {
                    it.thread = thread.toIntOrNull() ?: 0
                    it
                }
                .getOrElse {
                    runCatching { json.decodeFromString<ApiResponse.Failed>(responseBody) }
                        .getOrElse {
                            runCatching { json.decodeFromString<ApiResponse.InvalidType>(responseBody) }
                                .getOrElse {
                                    runCatching { json.decodeFromString<ApiResponse.Invalid>(responseBody) }
                                        .getOrDefault(ApiResponse.Invalid(responseBody))
                                }
                        }
                }
        } else {
            ApiResponse.Failed(reason = "Code: ${response.status.value}")
        }
    }

    override suspend fun getImageLoad(ctx: Context, request: ImageResponseModel): Long {
        val outputFile = request.getImageFile(ctx)
        Log.e("wtf", "getImageToCache request=$request")
        return withContext(Dispatchers.IO) {
            try {
                val response: ByteReadChannel = client.get(ApiRoutes.AI_TEMP_IMAGE) {
                    url {
                        parameters.apply {
                            append("imageId", request.imageId)
                        }
                    }
                }.bodyAsChannel()
                val bitmap = BitmapFactory.decodeStream(response.toInputStream())
                if (bitmap != null) {
                    FileOutputStream(outputFile).use { out ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
                        } else {
                            @Suppress("DEPRECATION")
                            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
                        }
                    }
                }
                outputFile.length()
            } catch (e: Exception) {
                e.printStackTrace()
                0L
            }
        }
    }

    override suspend fun checkSource(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = client.head(ApiRoutes.AI_SITE)
                response.status.value in 200..299
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}