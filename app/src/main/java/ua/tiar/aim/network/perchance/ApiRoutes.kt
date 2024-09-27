package ua.tiar.aim.network.perchance

object ApiRoutes {
    private const val HTTPS = "https://"
    private const val AI_DOMAIN = "perchance.org"
    const val AI_SITE = HTTPS + AI_DOMAIN
    private const val AI_IMAGE_GENERATION = HTTPS + "image-generation." + AI_DOMAIN
    private const val AI_IMAGE_GENERATED = HTTPS + "generated-images." + AI_DOMAIN
    private const val AI_API = "$AI_IMAGE_GENERATION/api"
    const val AI_GALLERY = "$AI_IMAGE_GENERATION/gallery"
    const val AI_IMAGE = "$AI_IMAGE_GENERATED/image"
    const val AI_GENERATE = "$AI_API/generate"
    const val AI_TEMP_IMAGE = "$AI_API/downloadTemporaryImage"

    const val AI_USER_ID = "$AI_API/getPublicUserId"
    const val AI_USER_VERIFY = "$AI_API/verifyUser"
    const val AI_EMBED = "$AI_IMAGE_GENERATION/embed"
}