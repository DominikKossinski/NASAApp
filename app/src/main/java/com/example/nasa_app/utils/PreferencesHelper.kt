package com.example.nasa_app.utils

import android.content.Context

class PreferencesHelper(applicationContext: Context) {

    companion object {
        private const val TOKEN = "TOKEN"
        private const val DAILY_NOTIFICATION_HOUR = "DAILY_NOTIFICATION_HOUR"
        private const val DAILY_NOTIFICATION_MINUTE = "DAILY_NOTIFICATION_MINUTE"
        private const val PREFERENCES = "com.example.nasa_app.utils.prefs"
    }

    private val preferences =
        applicationContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    var token: String? = null
        get() {
            return preferences.getString(TOKEN, null)
        }
        set(value) {
            field = value
            preferences.edit().putString(TOKEN, value).apply()
        }

    var dailyNotificationTime: Pair<Int, Int>? = null
        get() {
            val hour = preferences.getInt(DAILY_NOTIFICATION_HOUR, -1)
            val minute = preferences.getInt(DAILY_NOTIFICATION_MINUTE, -1)
            if (hour == -1 || minute == -1) return null
            return Pair(hour, minute)
        }
        set(value) {
            field = value
            preferences.edit().putInt(DAILY_NOTIFICATION_HOUR, value?.first ?: -1).apply()
            preferences.edit().putInt(DAILY_NOTIFICATION_MINUTE, value?.second ?: -1).apply()
        }

}