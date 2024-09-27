package ua.tiar.aim.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ua.tiar.aim.AppConstants
import ua.tiar.aim.data.models.ImageRequestModel
import ua.tiar.aim.repository.SettingsRepository

class AppSettings(private val context: Context): SettingsRepository {
    private val settings: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    //----------------------------------------------------------------------------------------------
    fun getLastPromptVal(): String {
        return getPref("last_prompt", "")
    }
    fun setLastPromptVal(value: String) {
        setPref("last_prompt", value)
    }
    //----------------------------------------------------------------------------------------------
    fun getLastSourceVal(): String {
        return getPref("last_source", "")
    }
    fun setLastSourceVal(value: String) {
        setPref("last_source", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastNegativePromptVal(): String {
        return getPref("last_negative_prompt", "")
    }
    fun setLastNegativePromptVal(value: String) {
        setPref("last_negative_prompt", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastSeedVal(): String {
        return getPref("last_seed", "")
    }
    fun setLastSeedVal(value: String) {
        setPref("last_seed", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastGuidanceScaleVal(): String {
        return getPref("last_guidance_scale", "")
    }
    fun setLastGuidanceScaleVal(value: String) {
        setPref("last_guidance_scale", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastStepsVal(): String {
        return getPref("last_steps", "")
    }
    fun setLastStepsVal(value: String) {
        setPref("last_steps", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastLoraScaleVal(): String {
        return getPref("last_loraScale", "")
    }
    fun setLastLoraScaleVal(value: String) {
        setPref("last_loraScale", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastModelVersionVal(): String {
        return getPref("last_modelVersion", "sd-1.5-dreamshaper-8")
    }
    fun setLastModelVersionVal(value: String) {
        setPref("last_modelVersion", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastInitImageVal(): String {
        return getPref("last_initImage", "")
    }
    fun setLastInitImageVal(value: String) {
        setPref("last_initImage", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastStrengthVal(): String {
        return getPref("last_strength", "")
    }
    fun setLastStrengthVal(value: String) {
        setPref("last_strength", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastResolutionVal(): String {
        return getPref("last_resolution", "512x768")
    }
    fun setLastResolutionVal(value: String) {
        setPref("last_resolution", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getLastCountVal(): Int {
        return getPref("last_count", 1)
    }
    fun setLastCountVal(value: Int) {
        setPref("last_count", value)
    }

    //----------------------------------------------------------------------------------------------
    fun getExtendEditVal(): Boolean {
        return getPref("extend_edit", false)
    }
    fun setExtendEditVal(value: Boolean) {
        setPref("extend_edit", value)
    }

    //----------------------------------------------------------------------------------------------
    fun removeLastVal() {
        listOf("last_count", "last_resolution", "last_strength", "last_initImage",
            "last_modelVersion", "last_loraScale", "last_steps", "last_guidance_scale",
            "last_seed", "last_negative_prompt", "last_source", "last_prompt").forEach {
                removeSetting(it)
        }
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    fun saveLast(request: ImageRequestModel, count: Int) {
        setLastPromptVal(request.prompt)
        setLastNegativePromptVal(request.negativePrompt)
        setLastSeedVal(request.seed.toString())
        setLastGuidanceScaleVal(request.guidanceScale.toString())
        setLastResolutionVal(request.resolution)
        setLastCountVal(count)
        setLastStepsVal(request.numSteps.toString())
        setLastLoraScaleVal(request.loraScale.toString())
        setLastSourceVal(request.source)
        setLastModelVersionVal(request.modelVersion)
        setLastInitImageVal(request.initImage)
        setLastStrengthVal(request.strength.toString())
    }
    //----------------------------------------------------------------------------------------------
    private val _isNeedSaveLastFlow = MutableStateFlow(isNeedSaveLast)
    override val isNeedSaveLastFlow: StateFlow<Boolean> = _isNeedSaveLastFlow
    override var isNeedSaveLast: Boolean
        get() = getPref("isNeedSaveLast", false)
        set(value) {
            setPref("isNeedSaveLast", value)
            removeLastVal()
            _isNeedSaveLastFlow.value = value
        }

    private val _userKeysFlow = MutableStateFlow(userKeys)
    override val userKeysFlow: StateFlow<Set<String>> = _userKeysFlow
    override var userKeys: Set<String>
        get() = getPref("userKeys", setOf(""))
        set(value) {
            setPref("userKeys", value)
            _userKeysFlow.value = value
        }

    private val _channelNameFlow = MutableStateFlow(channelName)
    override val channelNameFlow: StateFlow<String> = _channelNameFlow
    override var channelName: String
        get() = getPref("channelName", "ai-text-to-image-generator")
        set(value) {
            setPref("channelName", value)
            _channelNameFlow.value = value
        }

    private val _filteredNsfwFlow = MutableStateFlow(filteredNsfw)
    override val filteredNsfwFlow: StateFlow<Boolean> = _filteredNsfwFlow
    override var filteredNsfw: Boolean
        get() = getPref("filteredNsfw", true)
        set(value) {
            setPref("filteredNsfw", value)
            _filteredNsfwFlow.value = value
        }

    private val _curSourceFlow = MutableStateFlow(curSource)
    override val curSourceFlow: StateFlow<String> = _curSourceFlow
    override var curSource: String
        get() = getPref("current_source", AppConstants.PERCHANCE.lowercase()).lowercase()
        set(value) {
            setPref("current_source", value.lowercase())
            _curSourceFlow.value = value
        }

    private val _curOrderGalleryFlow = MutableStateFlow(curOrderGallery)
    override val curOrderGalleryFlow: StateFlow<String> = _curOrderGalleryFlow
    override var curOrderGallery: String
        get() = getPref("current_order_gallery", AppConstants.ORDER_TRENDING)
        set(value) {
            setPref("current_order_gallery", value)
            _curOrderGalleryFlow.value = value
        }

    //______________________________________________________________________________________________
    //______________________________________________________________________________________________
    //______________________________________________________________________________________________
    private fun setPref(key: String?, value: Any?) {
        try {
            settings.edit().apply {
                when (value) {
                    is Boolean -> putBoolean(key, value)
                    is Int -> putInt(key, value)
                    is String -> putString(key, value)
                    is Long -> putLong(key, value)
                    is Float -> putFloat(key, value)
                    is Set<*> -> putStringSet(key, value.filterIsInstance<String>().toSet())
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPref(key: String?, defValue: T): T {
        return try {
            val result: Any = when (defValue) {
                is Boolean -> settings.getBoolean(key, defValue)
                is Int -> settings.getInt(key, defValue)
                is String -> settings.getString(key, defValue) ?: defValue
                is Long -> settings.getLong(key, defValue)
                is Float -> settings.getFloat(key, defValue)
                is Set<*> -> settings.getStringSet(key, defValue.filterIsInstance<String>().toSet()) ?: defValue
                else -> throw IllegalArgumentException("Unsupported type")
            }
            result as T
        } catch (e: Exception) {
            e.printStackTrace()
            defValue
        }
    }

    private fun removeSetting(key: String?) {
        if (context.packageName != null) {
            try {
                settings.edit()?.remove(key)?.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}