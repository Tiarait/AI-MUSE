package ua.tiar.aim.repository

import android.content.Context
import ua.tiar.aim.data.models.ApiResponse
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel

interface ApiRepository {

    suspend fun checkSource(): Boolean

    suspend fun getPrompt(request: ImageRequestModel, channelName: String = "", userKey: String = "", nsfwFiltered: Boolean = true): ApiResponse

    suspend fun getUserVerify(token: String = "", thread: String = "0"): ApiResponse

    suspend fun getImageLoad(ctx: Context, request: ImageResponseModel): Long

    suspend fun getGallery(sort: String = "recent", timeRange: String = "all-time", hideIfScoreIsBelow: String = "-1", nsfwFiltered: Boolean = true, channel: String, skip: Int = 0): List<ImageResponseModel>
}