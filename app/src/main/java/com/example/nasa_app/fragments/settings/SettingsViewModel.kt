package com.example.nasa_app.fragments.settings

import androidx.lifecycle.viewModelScope
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val nasaNotificationsManager: NasaNotificationsManager,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val dailyNotificationTimeFlow = MutableSharedFlow<Pair<Int, Int>?>(1)

    init {
        refresh()
    }

    fun showDailyNotificationBottomSheet() {
        navigate(SettingsFragmentDirections.showDailyNotificationBottomSheet())
    }

    fun cancelDailyNotifications() {
        nasaNotificationsManager.setupDailyNotification(null)
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            dailyNotificationTimeFlow.emit(preferencesHelper.dailyNotificationTime)
        }
    }

}