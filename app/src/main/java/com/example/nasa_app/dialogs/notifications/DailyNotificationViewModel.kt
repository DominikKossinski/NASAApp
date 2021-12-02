package com.example.nasa_app.dialogs.notifications

import com.example.nasa_app.R
import com.example.nasa_app.architecture.BaseViewModel
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.room.AppDatabase
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class DailyNotificationViewModel @Inject constructor(
    private val nasaNotificationManager: NasaNotificationsManager,
    preferencesHelper: PreferencesHelper,
    appDatabase: AppDatabase
) : BaseViewModel(preferencesHelper, appDatabase) {

    val hourFlow = MutableStateFlow(preferencesHelper.dailyNotificationTime?.first ?: 0)
    val minuteFlow = MutableStateFlow(preferencesHelper.dailyNotificationTime?.second ?: 0)


    fun setupDailyNotification() {
        nasaNotificationManager.setupDailyNotification(Pair(hourFlow.value, minuteFlow.value))
        setToastMessage(R.string.daily_notification_set)
        navigateBack()
    }
}