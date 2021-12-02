package com.example.nasa_app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.utils.PreferencesHelper

class NasaBootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            setupDailyNotification(context)
        }
    }

    private fun setupDailyNotification(context: Context) {
        val preferencesHelper = PreferencesHelper(context)
        val dailyNotificationTime = preferencesHelper.dailyNotificationTime
        val nasaNotificationManager = NasaNotificationsManager(context, preferencesHelper)
        nasaNotificationManager.setupDailyNotification(dailyNotificationTime)
    }
}