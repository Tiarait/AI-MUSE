package ua.tiar.aim.network.artbreeder.models

import kotlinx.serialization.Serializable

@Serializable
data class Job(
    val name: String = "sd-lcm",//multi-ipa-light
    val data: JobData,
    val alias: String = "composer-image"
)