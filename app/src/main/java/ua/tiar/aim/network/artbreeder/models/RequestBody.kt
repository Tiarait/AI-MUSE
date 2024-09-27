package ua.tiar.aim.network.artbreeder.models

import kotlinx.serialization.Serializable

@Serializable
data class RequestBody(
    val job: Job,
    val environment: String? = null,
    val browserToken: String = ""
)