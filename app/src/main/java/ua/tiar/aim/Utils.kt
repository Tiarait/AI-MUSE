package ua.tiar.aim

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.core.content.FileProvider
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ua.tiar.aim.data.models.DialogAlertModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.data.models.MenuItem
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.security.MessageDigest
import kotlin.random.Random


object Utils {

    fun getAppVersion(ctx: Context): String {
        try {
            val info: PackageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            return info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return ""
        }
    }

    fun calculateAspectRatio(width: Int, height: Int): Pair<Int, Int> {
        val gcd = gcd(width, height)
        val simplifiedWidth = width / gcd
        val simplifiedHeight = height / gcd
        val ratio = simplifiedWidth.toDouble() / simplifiedHeight.toDouble()

        val commonRatios = listOf(
            21.0 / 9.0 to Pair(21, 9),
            9.0 / 21.0 to Pair(9, 21),
            16.0 / 9.0 to Pair(16, 9),
            9.0 / 16.0 to Pair(9, 16),
            16.0 / 10.0 to Pair(16, 10),
            10.0 / 16.0 to Pair(10, 16),
            4.0 / 3.0 to Pair(4, 3),
            3.0 / 4.0 to Pair(3, 4),
            5.0 / 3.0 to Pair(5, 3),
            3.0 / 5.0 to Pair(3, 5),
            21.0 / 9.0 to Pair(21, 9),
            3.0 / 2.0 to Pair(3, 2),
            2.0 / 3.0 to Pair(2, 3),
            1.0 to Pair(1, 1)
        )

        val closestRatio = commonRatios.minByOrNull { Math.abs(it.first - ratio) }
        return closestRatio?.second ?: Pair(simplifiedWidth, simplifiedHeight)
    }

    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    fun regexValJson(find: String, json: String): String {
        val pattern = """"$find":"(.*?)"""".toRegex()
        return pattern.find(json)?.groups?.get(1)?.value ?: ""
    }
    fun regexIntJson(find: String, json: String): String {
        val pattern = """"$find":(.*?),""".toRegex()
        val r = pattern.find(json)?.groups?.get(1)?.value ?: ""
        return if (r.isEmpty()) {
            val pattern2 = """"$find":([^"]*)\}""".toRegex()
            pattern2.find(json)?.groups?.get(1)?.value ?: ""
        } else {
            r
        }
    }

    fun regexVal(find: String, str: String): String {
        return regex("""$find="([^"]*)"""", str) ?: ""
    }

    fun regex(regex: String, str: String): String? {
        val matchResult = regex.toRegex().find(str)
        return matchResult?.groups?.get(1)?.value
    }

    fun <T> chooseRandom(vararg options: T): T {
        return options[Random.nextInt(options.size)]
    }

    fun getRandomBetween(a: Int, b: Int): Int {
        return if (Random.nextBoolean()) a else b
    }

    fun genImageMenu(ctx: Context, item: ImageResponseModel, filteredNsfw: Boolean): List<MenuItem> {
        val items: ArrayList<MenuItem> = ArrayList()
        val isNsfw = item.maybeNsfw && filteredNsfw
        val isInvalid = item.status == "" && !item.getImageFile(ctx).exists()
        if (!isNsfw && !isInvalid) items.add(
            MenuItem(id = R.string.download, title = ctx.getString(R.string.download), icon = Icons.Rounded.Download)
        )
        items.add(
            MenuItem(id = R.string.edit, title =  ctx.getString(R.string.edit), icon = Icons.Rounded.Edit)
        )
        if (!isNsfw && !isInvalid) items.add(
            MenuItem(id = R.string.share, title = ctx.getString(R.string.share), icon = Icons.Rounded.Share)
        )
        if (item.status != "web") items.add(
            MenuItem(id = R.string.delete, title = ctx.getString(R.string.delete), icon = Icons.Rounded.Delete)
        )
        return items.toList()
    }

    fun createImageRequest(ctx: Context, response: ImageResponseModel): ImageRequest {
        return ImageRequest.Builder(ctx)
            .data(
                if (response.status == "web") {
                    response.imageUrl
                } else {
                    response.getImageFile(ctx)
                }
            )
            .crossfade(true)
//            .memoryCachePolicy(CachePolicy.ENABLED)
//            .memoryCacheKey("${response.id}${response.imageId}")
            .build()
    }

    fun pausedBetweenClick(start: Long): Boolean {
        return System.currentTimeMillis() - start < 700
    }

    fun File.shareImage(ctx: Context): Intent {
        val uri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.fileprovider",
            this
        )

        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    suspend fun String.downloadImageToCache(ctx: Context, name: String): File? {
        val request = ImageRequest.Builder(ctx)
            .data(this)
            .build()
        val result = ctx.imageLoader.execute(request)
        val file = File(ctx.cacheDir, name)
        if (result is SuccessResult) {
            val bitmap = (result.drawable as BitmapDrawable).bitmap
            return withContext(Dispatchers.IO) {
                FileOutputStream(file).use { out ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
                    } else {
                        @Suppress("DEPRECATION")
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, out)
                    }
                }
                file
            }
        } else return null
    }

    suspend fun String.downloadImageToUri(ctx: Context, uri: Uri) {
        val request = ImageRequest.Builder(ctx)
            .data(this)
            .build()
        val result = ctx.imageLoader.execute(request)
        if (result is SuccessResult) {
            (result.drawable as BitmapDrawable).bitmap.saveToUri(ctx, uri)
        }
    }

    suspend fun Bitmap.saveToUri(ctx: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
                this@saveToUri.toInputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    suspend fun File.saveToUri(ctx: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            ctx.contentResolver.openOutputStream(uri)?.use { outputStream ->
                this@saveToUri.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    private fun Bitmap.toInputStream(): InputStream {
        val outputStream = ByteArrayOutputStream()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, outputStream)
        } else {
            @Suppress("DEPRECATION")
            compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        }
        return ByteArrayInputStream(outputStream.toByteArray())
    }

    fun dialogAlertDelete(ctx: Context, onPositiveClick : () -> Unit): DialogAlertModel {
        val dialogDelete = ctx.getString(R.string.delete)
        val dialogQuestion = ctx.getString(R.string.question)
        return DialogAlertModel(
            title = dialogDelete,
            message = dialogQuestion,
            positive = dialogDelete,
            onPositiveClick = onPositiveClick)
    }

    fun String.toMD5(): String =
        MessageDigest.getInstance("MD5")
            .digest(toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    fun String.append(append: String): String {
        return if (this.trim().isNotEmpty()) {
            this.trim().plus(if (!this.trim().endsWith(",")) " ," else " ").plus(append.trim())
        } else if (append.trim().isNotEmpty()) {
            append.trim()
        } else {
            this
        }
    }
}