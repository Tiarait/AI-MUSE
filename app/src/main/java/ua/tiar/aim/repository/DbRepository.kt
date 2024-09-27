package ua.tiar.aim.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ua.tiar.aim.data.models.ImageResponseIdCount
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.db.AppDatabase

class DbRepository(private val db: AppDatabase? = null) {

    fun getAllImageResponses(): Flow<List<ImageResponseModel>> {
        return db?.imageResponseDao()?.getAllImageResponses() ?: emptyFlow()
    }

    fun getAllImageResponseIdCount(): Flow<List<ImageResponseIdCount>> {
        return db?.imageResponseDao()?.getImageResponseIdCounts() ?: emptyFlow()
    }

    fun getSimilarImageResponses(promptHash: String): Flow<List<ImageResponseModel>> {
        return db?.imageResponseDao()?.getSimilarPrompt(promptHash) ?: emptyFlow()
    }

    fun getCopyImageResponses(imageId: String): Flow<List<ImageResponseModel>> {
        return db?.imageResponseDao()?.getCopyPrompt(imageId) ?: emptyFlow()
    }

    suspend fun writeRequest(request: ImageResponseModel): Long {
        return db?.imageResponseDao()?.insertImageResponse(request) ?: 0L
    }

    suspend fun updateRequest(request: ImageResponseModel) {
        db?.imageResponseDao()?.updateImageResponse(request)
    }

    suspend fun deleteResponses(requests: List<ImageResponseModel>) {
        db?.imageResponseDao()?.deleteImageResponses(requests)
    }

}