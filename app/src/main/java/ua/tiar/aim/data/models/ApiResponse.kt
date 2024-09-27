package ua.tiar.aim.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse(val status: String = "") {
    @Serializable
    data class Success(val data: ImageResponseModel) : ApiResponse()
    @Serializable
    data class User(var userStatus: String = "", val userKey: String, var thread: Int = -1) : ApiResponse(userStatus)
    @Serializable
    data class Failed(val reason: String) : ApiResponse("failed")
    @Serializable
    data class InvalidType(val type: Int) : ApiResponse()
    @Serializable
    data class Invalid(val error: String = "") : ApiResponse(error)
}