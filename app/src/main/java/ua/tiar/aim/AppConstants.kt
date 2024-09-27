package ua.tiar.aim

import androidx.compose.ui.unit.dp

object AppConstants {
    val minItemWidth = 132.dp
    val topBarHeight = 64.dp
    val topBarPadding = 8.dp
    val barRounded = 32.dp
    val prompt = ""//", cat, cat main character, story about cat"
    val negativePrompt = ""//"worst quality, poorly drawn, bad art, boring, deformed, bad composition, crappy artwork, bad lighting"
    val nsfwPrompt = "children, child, younger, junior, teenager, nsfw, nude, half nude, nudity, explicit, sexual, erotic, adult, inappropriate, indecent, lewd, obscene, revealing clothing, lingerie, tits, breast, bikini, scantily clad, cleavage, breasts, butt, genitals, private parts, suggestive, provocative, sexual content, hentai, r18, fetish, inappropriate content"
    val artStyles = listOf(
        "No style",
        "Painted Anime",
        "Digital Painting",
        "Painterly",
        "Casual Photo",
        "Cinematic",
        "Concept Art",
        "3D Disney Character",
        "2D Disney Character",
    )

    const val PERCHANCE = "Perchance"
    const val ARTBREEDER = "ArtBreeder"
    fun getSourceName(s: String?): String {
        return when(s?.lowercase()) {
            PERCHANCE.lowercase() -> PERCHANCE
            ARTBREEDER.lowercase() -> ARTBREEDER
            else -> ""
        }
    }

    const val ORDER_TOP = "Top"
    const val ORDER_RECENT = "Recent"
    const val ORDER_TRENDING = "Trending"
    const val ORDER_RANDOM = "Random"

    const val ERROR1 = "Error #1"
    const val ERROR2 = "Error #2"
    const val ERROR3 = "Error #3"
    const val ERROR4 = "Error #4"
    const val ERROR5 = "Error #5"
    const val ERROR6 = "Error #6"
    const val ERROR7 = "Error #7"
    const val ERROR8 = "Error #8"
    const val ERROR9 = "Error #9"
    const val ERROR10 = "Error #10"
}