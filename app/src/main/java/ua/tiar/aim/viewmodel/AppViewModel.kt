package ua.tiar.aim.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.Utils.downloadImageToCache
import ua.tiar.aim.Utils.downloadImageToUri
import ua.tiar.aim.Utils.saveToUri
import ua.tiar.aim.data.models.ApiResponse
import ua.tiar.aim.data.models.DialogAlertModel
import ua.tiar.aim.data.models.DialogModel
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.data.models.ImageResponseModel
import ua.tiar.aim.data.models.StatusRequestModel
import ua.tiar.aim.network.HttpClientProvider
import ua.tiar.aim.repository.ApiRepository
import ua.tiar.aim.repository.DbRepository
import ua.tiar.aim.repository.SettingsRepository
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

open class AppViewModel(
    private var apiRepository: ApiRepository? = null,
    private val dbRepository: DbRepository? = null,
    val settingsRepository: SettingsRepository? = null,
) : ViewModel() {
    private val json = Json { ignoreUnknownKeys = true }

    private val _tokenRequired = MutableStateFlow(false)
    val tokenRequired: StateFlow<Boolean> get() = _tokenRequired

    private val _imageResponses = MutableStateFlow<List<ImageResponseModel>>(emptyList())
    val imageResponses: StateFlow<List<ImageResponseModel>> get() = _imageResponses
    private val _imageIdCounts = MutableStateFlow<Map<String, Int>>(emptyMap())

    private val _galleryResponses = MutableStateFlow<List<ImageResponseModel>>(emptyList())
    val galleryResponses: StateFlow<List<ImageResponseModel>> get() = _galleryResponses

    private val _selectedItems = MutableStateFlow<List<ImageResponseModel>>(emptyList())
    val selectedItems: StateFlow<List<ImageResponseModel>> get() = _selectedItems

    private val _detailItem = MutableStateFlow<ImageResponseModel?>(null)
    val detailItem: StateFlow<ImageResponseModel?> get() = _detailItem

    private val _alertDialogs = MutableStateFlow<List<DialogAlertModel>>(emptyList())
    val alertDialogs: StateFlow<List<DialogAlertModel>> get() = _alertDialogs

    private val _dialogs = MutableStateFlow<List<DialogModel>>(emptyList())
    val dialogs: StateFlow<List<DialogModel>> get() = _dialogs

    private val _settingsScreen = MutableStateFlow(false)
    val settingsScreen: StateFlow<Boolean> get() = _settingsScreen

    var bottomScreen = mutableStateOf(false)

    var isPromptLoading = mutableStateOf(false)
    var isExternalGalleryLoading = mutableStateOf(false)

    private val lastSkipGallery = mutableIntStateOf(-1)
    private val _status = MutableStateFlow<StatusRequestModel?>(null)
    val status: StateFlow<StatusRequestModel?> get() = _status


    private val _isSplashShow = MutableStateFlow(true)
    val isSplashShow: StateFlow<Boolean> get() = _isSplashShow
    private val _isMainShow = MutableStateFlow(false)
    val isMainShow: StateFlow<Boolean> get() = _isMainShow

    var titleBarValue = mutableStateOf("")
    var isShowTitleBar = mutableStateOf(false)

    init {
//        getUserVerify()
        viewModelScope.launch {
            dbRepository?.getAllImageResponses()?.collect {
                _imageResponses.value = it
            }
        }
        viewModelScope.launch {
            dbRepository?.getAllImageResponseIdCount()?.collect { list ->
                val map = list.associate { it.imageId to it.id }
                _imageIdCounts.value = map
            }
        }
        viewModelScope.launch {
            delay(3200)
            hideSplash()
        }
    }

    fun hideSplash() {
        _isSplashShow.value = false
        viewModelScope.launch {
            delay(1000)
            _isMainShow.value = true
        }
    }

    fun showSettings() {
        _settingsScreen.value = true
    }
    fun hideSettings() {
        _settingsScreen.value = false
    }

    fun updGallery() {
        getGalleryJob?.cancel()
        lastSkipGallery.intValue = 0
        _galleryResponses.value = emptyList()
        isExternalGalleryLoading.value = false
    }

    fun changeSource() {
        apiRepository = when(settingsRepository?.curSource?.lowercase()) {
            AppConstants.PERCHANCE.lowercase() -> ua.tiar.aim.network.perchance.ApiService(HttpClientProvider.provideHttpClient())
            else -> ua.tiar.aim.network.artbreeder.ApiService(HttpClientProvider.provideHttpClient())
        }
        stopAllPrompt()
        settingsRepository?.userKeys = emptySet()
        updGallery()
    }

    fun hasImageDuplicates(imageId: String): Boolean {
        return _imageIdCounts.value[imageId] != null
    }

    private var getGalleryJob: Job? = null
    fun getGallery(
        refresh: Boolean = false,
        skip: Int = 0,
        onFinished: ((Boolean) -> Unit)? = null
    ): Job {
        getGalleryJob = viewModelScope.launch {
            if (isExternalGalleryLoading.value) return@launch

            isExternalGalleryLoading.value = true

            val list = try {
                val l = withContext(Dispatchers.IO) {
                    apiRepository?.getGallery(
                        sort = settingsRepository?.curOrderGallery ?: AppConstants.ORDER_TRENDING,
                        channel = settingsRepository?.channelName.orEmpty(),
                        nsfwFiltered = settingsRepository?.filteredNsfw ?: true,
                        skip = skip
                    ) ?: emptyList()
                }.also {
                    _galleryResponses.value = if (refresh) {
                        lastSkipGallery.intValue = 0
                        it
                    } else if (lastSkipGallery.intValue != skip) {
                        lastSkipGallery.intValue = skip
                        val currentImageIds = _galleryResponses.value.map { img -> img.imageId }.toSet()
                        _galleryResponses.value + it.filterNot { img -> img.imageId in currentImageIds }
                    } else _galleryResponses.value
                }
                delay(800)
                l
            } catch (e: Exception) {
                e.printStackTrace()
                addAlertDialog(
                    DialogAlertModel(
                        title = AppConstants.ERROR9,
                        message = e.message.orEmpty(),
                        positive = "OK",
                        cancelable = false
                    )
                )
                emptyList()
            }

            isExternalGalleryLoading.value = false
            onFinished?.invoke(list.isNotEmpty())
        }
        return getGalleryJob!!
    }

    private val lastPromptsIds: HashMap<String, Long> = HashMap()
    private var getPromptsJob: Job? = null
    fun getPrompts(ctx: Context?, request: ImageRequestModel, counts: Int) {
        getPromptsJob = viewModelScope.launch(Dispatchers.IO) {
            isPromptLoading.value = true
            try {
                updStatus(action = R.string.status_check_verify)
                if (settingsRepository?.curSource == AppConstants.ARTBREEDER.lowercase()) {
                    startImageGeneration(ctx, request, counts)
                } else {
                    val verify = getUserVerifyCheck(withContext(Dispatchers.IO) {
                        apiRepository?.getUserVerify()
                    })

                    if (verify is ApiResponse.Failed && !_tokenRequired.value &&
                        (verify.reason == "token_required" || verify.reason == "invalid_token")
                    ) {
                        handleTokenVerification(ctx)
                    }

                    val userKeys = settingsRepository?.userKeys.orEmpty()
                    if (userKeys.isNotEmpty()) {
                        val keySize = userKeys.size
                        if (keySize < counts) {
                            generateUserKeys(counts, keySize)
                        }

                        if (settingsRepository!!.userKeys.size < counts) {
                            showAlertDialog(
                                ctx,
                                title = AppConstants.ERROR7,
                                message = ctx?.getString(R.string.status_user_keys) + " [${userKeys.size}/$counts]"
                            )
                        } else {
                            startImageGeneration(ctx, request, counts)
                        }
                    } else {
                        showAlertDialog(ctx, title = AppConstants.ERROR9, message = "Empty user keys")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showAlertDialog(ctx, title = AppConstants.ERROR9, message = e.message.orEmpty())
            } finally {
                lastPromptsIds.clear()
                _status.value = null
                isPromptLoading.value = false
            }
        }
    }

    private suspend fun handleTokenVerification(ctx: Context?) {
        updStatus(status = R.string.status_new_token)
        _tokenRequired.value = true
        var delayCount = 0
        while (_tokenRequired.value && delayCount < 6) {
            delay(1000)
            delayCount++
        }
        if (delayCount == 10 && settingsRepository?.userKeys?.firstOrNull().isNullOrEmpty()) {
            showAlertDialog(ctx, title = AppConstants.ERROR5, message = ctx?.getString(R.string.status_new_token_failed).orEmpty())
        }
    }

    private suspend fun generateUserKeys(counts: Int, keySize: Int) {
        updStatus(status = R.string.status_user_keys, reason = "$keySize/$counts")
        val usedThreads = settingsRepository?.userKeys?.mapNotNull { key ->
            """thread":(\d+)""".toRegex().find(key)?.groupValues?.get(1)?.toInt()
        }.orEmpty().toSet()

        val threads = generateSequence(0) { it + 1 }
            .dropWhile { it in usedThreads }
            .take(counts - keySize + 1)
            .toList()

        val keys = withContext(Dispatchers.IO) {
            threads.map { thread ->
                async { getUserKey(thread, counts) }
            }
        }
        keys.awaitAll()
    }

    private suspend fun startImageGeneration(ctx: Context?, request: ImageRequestModel, counts: Int) {
        updStatus(action = R.string.status_generate_images, reason = "0/$counts")
        val completedTasks = AtomicInteger(0)

        val tasks = withContext(Dispatchers.IO) {
            List(counts) {
                async {
                    getPrompt(ctx, request, it)
                    updStatus(reason = "${completedTasks.incrementAndGet()}/$counts")
                }
            }
        }
        tasks.awaitAll()
    }


    private fun showAlertDialog(ctx: Context?, title: String, message: String) {
        addAlertDialog(
            DialogAlertModel(
                title = title,
                message = message,
                positive = ctx?.getString(R.string.ok) ?: "OK",
                cancelable = false
            )
        )
    }


    fun stopAllPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            getPromptsJob?.cancel()
            _status.value = null
            isPromptLoading.value = false
        }
    }

    private suspend fun getUserKey(thread: Int, counts: Int, tried: Int = 0) {
        try {
            val userKey = withContext(Dispatchers.IO) {
                apiRepository?.getUserVerify(
                    token = "",
                    thread = thread.toString()
                )
            }
            if (userKey is ApiResponse.User) {
                settingsRepository?.userKeys = userKey.toSet(json, settingsRepository?.userKeys)
                updStatus(
                    status = R.string.status_user_keys,
                    reason = "${settingsRepository?.userKeys?.size}/$counts"
                )
            } else if (tried == 0) {
                getUserKey(thread, counts, 1)
            } else {
                Log.e("wtf", "async userKey $thread ${userKey.toString()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            addAlertDialog(
                DialogAlertModel(
                    title = AppConstants.ERROR9,
                    message = e.message.toString(),
                    positive = "OK",
                    cancelable = false)
            )
        }
    }

    private suspend fun getPrompt(ctx: Context?,
                                  request: ImageRequestModel,
                                  current: Int = 0,
                                  tried: Int = 0,
                                  oldId: Long = 0) {
        withContext(Dispatchers.IO) {
            val id = if (tried == 0) {
                dbRepository?.writeRequest(
                    ImageResponseModel(
                        prompt = request.prompt,
                        width = request.resolution.split("x").first().toIntOrNull() ?: 1024,
                        height = request.resolution.split("x").last().toIntOrNull() ?: 1024,
                    )
                ) ?: 0L
            } else oldId

            try {
                val userKey = settingsRepository?.userKeys?.elementAtOrNull(current)
                    ?: settingsRepository?.userKeys?.firstOrNull() ?: "0"
                if (userKey.isNotEmpty() && settingsRepository != null && ctx != null) {
                    val result = apiRepository?.getPrompt(
                        request,
                        settingsRepository.channelName,
                        userKey.decodeUserKey(json),
                        settingsRepository.filteredNsfw
                    )
                    if (result is ApiResponse.Success) {
                        if ((hasImageDuplicates(result.data.imageId) || lastPromptsIds.contains(
                                result.data.imageId
                            )) && tried < 1
                        ) {
                            getPrompt(ctx, request, current, tried + 1, id)
                        } else {
                            getImageLoad(ctx = ctx, id = id, response = result.data)
                        }
                    } else {
                        if (result is ApiResponse.Invalid) {
                            settingsRepository.userKeys =
                                settingsRepository.userKeys.toList().minus(userKey).toSet()
                            if (tried > 0) {
                                addAlertDialog(
                                    DialogAlertModel(
                                        title = AppConstants.ERROR6,
                                        message = "${result.status} [$tried]",
                                        positive = "Ok",
                                        cancelable = false
                                    )
                                )
                            } else {
                                getPrompt(ctx, request, current, tried + 1, id)
                            }
                        } else {
                            if (tried > 0) {
                                addAlertDialog(
                                    DialogAlertModel(
                                        title = AppConstants.ERROR8,
                                        message = "${result?.status} [$tried]",
                                        positive = "Ok",
                                        cancelable = false
                                    )
                                )
                            } else {
                                getPrompt(ctx, request, current, tried + 1, id)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addAlertDialog(
                    DialogAlertModel(
                        title = AppConstants.ERROR9,
                        message = e.message.toString(),
                        positive = "OK",
                        cancelable = false)
                )
            }
        }
    }
    private fun checkSource() {
        viewModelScope.launch {
            val r = try {
                withContext(Dispatchers.IO) {
                    apiRepository?.checkSource() ?: false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            if (!r) {
                //TODO strings
                addAlertDialog(
                    DialogAlertModel(
                        title = AppConstants.ERROR10,
                        message = "Can`t connect to the «${AppConstants.getSourceName(settingsRepository?.curSource)}»",
                        negative = "Skip",
                        positive = "Change source",
                        onPositiveClick = {
                            val s = settingsRepository?.curSource?.lowercase()
                            val nS = if (s != AppConstants.PERCHANCE.lowercase())
                                AppConstants.PERCHANCE else AppConstants.ARTBREEDER
                            settingsRepository?.curSource = nS.lowercase()
                            checkSource()
                        },
                        cancelable = false)
                )
            }
        }
    }

    fun getUserVerify(token: String = "", thread: String = "0") {
        viewModelScope.launch {
            try {
                val r = withContext(Dispatchers.IO) {
                    apiRepository?.getUserVerify(token, thread)
                }
                val verify = getUserVerifyCheck(r)
                if (verify is ApiResponse.Failed &&
                    !_tokenRequired.value &&
                    (verify.reason == "token_required" || verify.reason == "invalid_token")
                ) {
                    _tokenRequired.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addAlertDialog(
                    DialogAlertModel(
                        title = AppConstants.ERROR9,
                        message = e.message.toString(),
                        positive = "OK",
                        cancelable = false)
                )
            }
            _tokenRequired.value = false
        }
    }

    private suspend fun updStatus(@StringRes action: Int? = null, @StringRes status: Int? = null, reason: String? = null) {
        withContext(Dispatchers.IO) {
            val newStatus = _status.value?.copy(
                action = action ?: (_status.value?.action ?: -1),
                status = status,
                reason = reason
            ) ?: StatusRequestModel(action ?: -1, status, reason)
            _status.update { newStatus }
        }
    }

    private fun getUserVerifyCheck(response: ApiResponse?): ApiResponse? {
        when (response) {
            is ApiResponse.User -> {
                settingsRepository?.userKeys = response.toSet(json, settingsRepository?.userKeys)
            }
            is ApiResponse.Failed -> {
                settingsRepository?.userKeys = setOf("")
                if (!_tokenRequired.value && (response.reason == "token_required" || response.reason == "invalid_token")) {
                    return response
                } else {
                    addAlertDialog(
                        DialogAlertModel(
                        title = AppConstants.ERROR1,
                        message = response.reason,
                        positive = "Ok",
                        cancelable = false)
                    )
                }
            }
            is ApiResponse.Invalid -> {
                settingsRepository?.userKeys = setOf("")
                addAlertDialog(
                    DialogAlertModel(
                    title = AppConstants.ERROR2,
                    message = response.status,
                    positive = "Ok",
                    cancelable = false)
                )
            }
            else -> {
                settingsRepository?.userKeys = setOf("")
                addAlertDialog(
                    DialogAlertModel(
                    title = AppConstants.ERROR3,
                    message = "unknown",
                    positive = "Ok",
                    cancelable = false)
                )
            }
        }
        return response
    }

    private suspend fun getImageLoad(ctx: Context, id: Long, response: ImageResponseModel) {
        response.id = id
        if (response.maybeNsfw && settingsRepository?.filteredNsfw == true) {
            addAlertDialog(
                DialogAlertModel(
                title = AppConstants.ERROR4,
                message = ctx.getString(R.string.status_nsfw),
                positive = ctx.getString(R.string.ok),
                cancelable = false)
            )
        } else {
            try {
                val existId = _imageIdCounts.value[response.imageId]
                if (existId == null && !lastPromptsIds.contains(response.imageId)) {
                    lastPromptsIds[response.imageId] = id
                    val r = withContext(Dispatchers.IO) {
                        apiRepository?.getImageLoad(ctx, response)
                    }
                    Log.e("wtf", "Load Image $id [$r byte]")
                } else {
                    val f = File(
                        ctx.filesDir,
                        "${existId}-${response.imageId}.${response.fileExtension}"
                    )
                    if (f.exists()) f.copyTo(response.getImageFile(ctx))
                    else {
                        val f2 = File(
                            ctx.filesDir,
                            "${lastPromptsIds[response.imageId]}-${response.imageId}.${response.fileExtension}"
                        )
                        if (!f2.exists() || f2.length() == 0L) delay(500)
                        if (!f2.exists() || f2.length() == 0L) delay(500)
                        if (f2.exists()) f2.copyTo(response.getImageFile(ctx))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                addAlertDialog(
                    DialogAlertModel(
                        title = AppConstants.ERROR9,
                        message = e.message.toString(),
                        positive = "OK",
                        cancelable = false)
                )
            }
        }
        dbRepository?.updateRequest(response)
    }

    fun addAlertDialog(dialog: DialogAlertModel) {
        viewModelScope.launch {
            _alertDialogs.update { currentList ->
                currentList.toMutableList().apply {
                    if (!contains(dialog)) add(dialog)
                }
            }
        }
    }

    fun removeAlertDialog(dialog: DialogAlertModel) {
        viewModelScope.launch {
            _alertDialogs.value -= dialog
        }
    }


    fun addDialog(dialog: DialogModel) {
        viewModelScope.launch {
            _dialogs.update { currentList ->
                currentList.toMutableList().apply {
                    if (!contains(dialog)) add(dialog)
                }
            }
        }
    }

    fun removeDialog(dialog: DialogModel) {
        viewModelScope.launch {
            _dialogs.value -= dialog
        }
    }

    fun selectDetailItem(item: ImageResponseModel?, withDelay: Long = 0L) {
        viewModelScope.launch {
            if (withDelay != 0L) delay(withDelay)
            _detailItem.value = item
        }
    }

    fun deleteDetailItem(ctx: Context) {
        viewModelScope.launch {
            dbRepository?.deleteResponses(listOf(_detailItem.value!!))
            _detailItem.value?.getImageFile(ctx)?.delete()
            _detailItem.value = null
            Toast.makeText(ctx, ctx.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
        }
    }

    fun selectItem(item: ImageResponseModel) {
        viewModelScope.launch {
            _selectedItems.update { currentList ->
                currentList.toMutableList().apply {
                    if (contains(item)) remove(item) else add(item)
                }
            }
        }
    }

    fun clearSelects() {
        viewModelScope.launch {
            _selectedItems.value = emptyList()
        }
    }

    //TODO move to utils
    fun deleteSelects(ctx: Context) {
        viewModelScope.launch {
            dbRepository?.deleteResponses(_selectedItems.value)
            _selectedItems.value.forEach { response ->
                response.getImageFile(ctx).delete()
            }
            _selectedItems.value = emptyList()
            Toast.makeText(ctx, ctx.getString(R.string.deleted), Toast.LENGTH_SHORT).show()
        }
    }

    //TODO move to utils
    fun downloadImages(ctx: Context, uri: Uri, list: List<ImageResponseModel>) {
        viewModelScope.launch {
            if (list.size == 1) {
                list.first().let {
                    if (it.status == "web") it.imageUrl
                    else it.getImageFile(ctx).absolutePath
                }.downloadImageToUri(ctx, uri)
            } else {
                val files = list.map {
                    if (it.status == "web") it.imageUrl.downloadImageToCache(ctx, "${it.imageId}.${it.fileExtension}")
                    else it.getImageFile(ctx)
                }
                val outputFile = File(ctx.cacheDir, "${System.currentTimeMillis()}.zip")
                ZipOutputStream(FileOutputStream(outputFile)).use { zipOut ->
                    files.forEach { file -> if (file != null) {
                        FileInputStream(file).use { fis ->
                            val zipEntry = ZipEntry(file.name)
                            zipOut.putNextEntry(zipEntry)

                            fis.copyTo(zipOut)
                            zipOut.closeEntry()
                        }
                    } }
                }
                outputFile.saveToUri(ctx, uri)
                delay(500)
                outputFile.delete()
            }
            _selectedItems.value = emptyList()
            Toast.makeText(ctx, ctx.getString(R.string.done), Toast.LENGTH_SHORT).show()
        }
    }


    fun similarImageResponses(promptHash: String): Flow<List<ImageResponseModel>> {
        if (promptHash.isEmpty()) return emptyFlow()
        return dbRepository?.getSimilarImageResponses(promptHash) ?: emptyFlow()
    }


    fun copyImageResponses(imageId: String): Flow<List<ImageResponseModel>> {
        if (imageId.isEmpty()) return emptyFlow()
        return dbRepository?.getCopyImageResponses(imageId) ?: emptyFlow()
    }
}

private fun ApiResponse.User.toSet(json: Json, parent: Set<String>?): Set<String> {
    this.userStatus = ""
    return if (parent?.firstOrNull()?.isEmpty() == true)
        setOf(json.encodeToString(this))
    else parent?.toList()?.plus(json.encodeToString(this))?.toSet() ?: parent ?: setOf("")
}

private fun String.decodeUserKey(json: Json): String {
    return try {
        json.decodeFromString<ApiResponse.User>(this).userKey
    } catch (e: Exception) {
        "0"
    }
}
