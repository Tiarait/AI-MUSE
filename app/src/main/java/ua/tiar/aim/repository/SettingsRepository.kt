package ua.tiar.aim.repository

import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val isNeedSaveLastFlow: StateFlow<Boolean>
    var isNeedSaveLast: Boolean

    val userKeysFlow: StateFlow<Set<String>>
    var userKeys: Set<String>

    val channelNameFlow: StateFlow<String>
    var channelName: String

    val filteredNsfwFlow: StateFlow<Boolean>
    var filteredNsfw: Boolean

    val curSourceFlow: StateFlow<String>
    var curSource: String

    val curOrderGalleryFlow: StateFlow<String>
    var curOrderGallery: String

}