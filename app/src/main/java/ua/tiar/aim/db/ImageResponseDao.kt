package ua.tiar.aim.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ua.tiar.aim.data.models.ImageResponseIdCount
import ua.tiar.aim.data.models.ImageResponseModel

@Dao
interface ImageResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageResponse(imageResponse: ImageResponseModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateImageResponse(imageResponse: ImageResponseModel)

    @Query("SELECT * FROM image_response ORDER BY id DESC")
    fun getAllImageResponses(): Flow<List<ImageResponseModel>>

    @Query("SELECT imageId, id, COUNT(*) as count FROM image_response GROUP BY imageId HAVING COUNT(*) > 1")
    fun getImageResponseIdCounts(): Flow<List<ImageResponseIdCount>>

    @Query("SELECT * FROM image_response WHERE prompt LIKE :prompt AND (seed = :seed OR guidanceScale = :guidanceScale) ORDER BY id DESC")
    fun getSimilar(prompt: String, seed: Long, guidanceScale: Int): Flow<List<ImageResponseModel>>

    @Query("SELECT * FROM image_response WHERE promptHash LIKE :promptHash ORDER BY id DESC")
    fun getSimilarPrompt(promptHash: String): Flow<List<ImageResponseModel>>

    @Query("SELECT * FROM image_response WHERE imageId LIKE :imageId ORDER BY id DESC")
    fun getCopyPrompt(imageId: String): Flow<List<ImageResponseModel>>

    @Delete
    suspend fun deleteImageResponses(images: List<ImageResponseModel>)
}