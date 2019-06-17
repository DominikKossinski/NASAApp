package com.example.nasa_app

import android.app.Application
import android.content.Intent
import android.util.Log


class NasaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:NasaApp", "onCreate()")
        }
        val serviceIntent = Intent(this, AppService::class.java)
        startService(serviceIntent)
    }

    override fun onTerminate() {
        if (BuildConfig.DEBUG) {
            Log.d("MyLog:NasaApp", "onTerminate()")
        }
        super.onTerminate()
    }
}