package ua.tiar.aim.data.models

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import ua.tiar.aim.AppConstants
import java.io.File

@Serializable @Immutable
@Entity(tableName = "image_response")
data class ImageResponseModel (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    val status: String = "",
    val source: String = "",
    val imageId: String = "",
    val imageUrl: String = "",
    val modelVersion: String = "",
    val fileExtension: String = "",
    val seed: Long = -1,
    val prompt: String = "",
    val promptHash: String = "",
    val width: Int,
    val height: Int,
    val guidanceScale: Float = 7f,
    val numSteps: Int = 20,
    val strength: Float = 1f,
    val loraScale: Float = 1f,
    val initImage: String = "",
    val negativePrompt: String = "",
    val maybeNsfw: Boolean = false
) {
    @Ignore
    fun getDonePrompt() :String {
        return prompt.replaceFirst(AppConstants.prompt, "")
    }

    @Ignore
    fun getDoneNegativePrompt() :String {
        return negativePrompt.replaceFirst(AppConstants.negativePrompt, "")
    }

    @Ignore
    fun getImageFile(ctx: Context) :File {
        return File(ctx.filesDir, "$id-$imageId.$fileExtension")
    }
}