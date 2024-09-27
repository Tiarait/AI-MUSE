package ua.tiar.aim.network.artbreeder

import ua.tiar.aim.AppConstants

object ApiRoutes {
    private const val HTTPS = "https://"
    private const val AI_DOMAIN = "www.artbreeder.com"
    const val AI_SITE = HTTPS + AI_DOMAIN
    private const val AI_API = "$AI_SITE/api"

    const val AI_GENERATE = "$AI_API/realTimeJobs"

    private const val AI_GALLERY_FEATURE = "$AI_SITE/beta/api/images/featured.json"
    private const val AI_GALLERY_TRENDING = "$AI_SITE/trending"
    private const val AI_GALLERY_RECENT = "$AI_SITE/images"

    fun getGallery(sort: String = AppConstants.ORDER_TRENDING): String {
        return when(sort) {
//            AppConstants.ORDER_TOP -> AI_GALLERY_FEATURE
            AppConstants.ORDER_TRENDING -> AI_GALLERY_TRENDING
            else -> AI_GALLERY_RECENT
        }
    }
}