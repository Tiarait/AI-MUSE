package ua.tiar.aim.network.artbreeder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.tiar.aim.AppConstants
import ua.tiar.aim.Utils
import ua.tiar.aim.Utils.toMD5
import ua.tiar.aim.data.models.ApiResponse
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.network.artbreeder.models.GalleryResponse
import ua.tiar.aim.network.artbreeder.models.Job
import ua.tiar.aim.network.artbreeder.models.JobData
import ua.tiar.aim.network.artbreeder.models.RequestBody
import ua.tiar.aim.repository.ApiRepository
import java.io.FileOutputStream
import kotlin.random.Random

class ApiService(private val client: HttpClient): ApiRepository {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    override suspend fun getGallery(sort: String, timeRange: String, hideIfScoreIsBelow: String, nsfwFiltered: Boolean, channel: String, skip: Int): List<ImageResponseModel> {
        val t = System.currentTimeMillis()
        val s = if (sort == AppConstants.ORDER_TOP) AppConstants.ORDER_RANDOM else sort
        val response = client.post(ApiRoutes.getGallery(s)) {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            val random = """
                ,"order_by":"random"
            """.trimIndent()
            setBody("""
                {"offset":${skip},"limit":90,"tag_search_type":"substring","models":["prompter"],"tags":[],"tagged_by":null
                ${if (sort == AppConstants.ORDER_TOP) random else ""}}
            """.trimIndent())
        }
        val responseBody: String = response.bodyAsText()
        Log.e("wtf", "getGallery responseBody=${responseBody.length} ${((System.currentTimeMillis() - t)/1000).toInt()}")
        try {
            val serverResponses: List<GalleryResponse> = json.decodeFromString(responseBody)
            return serverResponses.mapIndexed { index, galleryResponse -> galleryResponse.toImageResponseModel(index.toLong()) }
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList<ImageResponseModel>().toList()
        }
    }

    override suspend fun getPrompt(request: ImageRequestModel, channelName: String, userKey: String, nsfwFiltered: Boolean): ApiResponse {
        var seed = request.seed
        if (seed == -1L) seed = Random.nextLong(from = 0, until = 1000000L)
        var modelVersion = "sd-1.5-dreamshaper-8"
        if (request.modelVersion.isNotEmpty()) modelVersion = request.modelVersion
        val data = if (request.modelVersion == "sdxl-lightning") {
            Job(name = "multi-ipa-light", data = JobData.JobDataComposer(
                seed = seed,
                prompt = request.prompt,
                width = request.resolution.split("x").first().toIntOrNull() ?: 1024,
                height = request.resolution.split("x").last().toIntOrNull() ?: 1024,
                negative_prompt = request.negativePrompt
            ))
        } else {
            Job(alias = "", data = JobData.JobDataPrompter(
                model_version = modelVersion,
                seed = seed,
                lcm_lora_scale = request.loraScale,
                num_steps = request.numSteps,
                prompt = request.prompt,
                guidance_scale = request.guidanceScale,
                init_image = request.initImage.ifEmpty { null },
                strength = request.strength,
                width = request.resolution.split("x").first().toIntOrNull() ?: 1024,
                height = request.resolution.split("x").last().toIntOrNull() ?: 1024,
                negative_prompt = request.negativePrompt
            ))
        }
        val requestBody = RequestBody(job = data)
        val body = json.encodeToString(requestBody).replace("\\\\", "\\")
        val response = client.post(ApiRoutes.AI_GENERATE) {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(body)
        }
        val responseBody: String = response.bodyAsText()
        Log.e("wtf", "getPrompt request=$request")
        Log.e("wtf", "getPrompt body=$body")
        Log.e("wtf", "getPrompt responseBody=$responseBody")
        return if (responseBody.isNotEmpty()) {
            val imageUrl = Utils.regexValJson("url", responseBody)
            if (imageUrl.isNotEmpty()) {
                var imageId = imageUrl.split("/").lastOrNull()
                if (imageId != null && imageId.contains(".")) imageId = imageId.split(".").first()
                ApiResponse.Success(ImageResponseModel(
                    id = request.id,
                    status = "",
                    source = AppConstants.ARTBREEDER,
                    imageUrl = imageUrl,
                    imageId = imageId ?: imageUrl,
                    fileExtension = "jpeg",
                    prompt = request.prompt,
                    promptHash = request.prompt.filter { it.isLetterOrDigit() }.toMD5(),
                    negativePrompt = request.negativePrompt,
                    seed = seed,
                    numSteps = request.numSteps,
                    guidanceScale = request.guidanceScale,
                    loraScale = request.loraScale,
                    initImage = request.initImage,
                    strength = request.strength,
                    modelVersion = modelVersion,
                    maybeNsfw = responseBody.contains("isNSFW\":true") || responseBody.contains("is_nsfw\":true"),
                    width = request.resolution.split("x").first().toIntOrNull() ?: 1024,
                    height = request.resolution.split("x").last().toIntOrNull() ?: 1024,
                ))
            } else {
                val message = Utils.regexValJson("message", responseBody)
                if (message.isNotEmpty()) {
                    ApiResponse.Invalid(message)
                } else {
                    ApiResponse.Invalid(responseBody)
                }
            }
        } else {
            ApiResponse.Failed(reason = "Code: ${response.status.value}")
        }
    }

    override suspend fun getUserVerify(token: String, thread: String): ApiResponse {
        return ApiResponse.User("ignore", "ignore${Random.nextLong(from = 0, until = 1000000L)}", thread.toIntOrNull() ?: 0)
    }

    override suspend fun getImageLoad(ctx: Context, request: ImageResponseModel): Long {
        val outputFile = request.getImageFile(ctx)
        Log.e("wtf", "getImageToCache request=$request")
        return withContext(Dispatchers.IO) {
            try {
                val response: ByteReadChannel = client.get(request.imageUrl).bodyAsChannel()
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