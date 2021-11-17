package com.example.nasa_app.utils

import android.content.Context

class PreferencesHelper(applicationContext: Context) {

    companion object {
        internal const val TOKEN = "TOKEN"
        internal const val PREFERENCES = "com.example.nasa_app.utils.prefs"
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
}