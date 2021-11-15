package com.example.nasa_app.application

import android.app.Application
import android.content.Intent
import android.util.Log
import com.example.nasa_app.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NasaApp : Application()