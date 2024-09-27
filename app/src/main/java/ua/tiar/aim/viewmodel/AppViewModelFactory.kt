package ua.tiar.aim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ua.tiar.aim.repository.ApiRepository
import ua.tiar.aim.repository.DbRepository
import ua.tiar.aim.repository.SettingsRepository

class AppViewModelFactory(
    private val databaseRepository: DbRepository? = null,
    private val networkRepository: ApiRepository? = null,
    private val settingsRepository: SettingsRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(networkRepository, databaseRepository, settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}