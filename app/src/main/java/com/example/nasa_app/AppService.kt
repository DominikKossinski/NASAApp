package com.example.nasa_app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.nasa_app.asynctasks.LogOutAsyncTask
import java.util.concurrent.Semaphore

class AppService : Service() {
    private val TAG = "MyLog:AppService"

    companion object {
        var jsessionid = ""
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand()")
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val semaphore = Semaphore(1)
        if (rootIntent.component != null) {
            val comp = rootIntent.component!!
            val className = comp.className
            if (className.contentEquals("com.example.nasa_app.activities.LauncherActivity")) {
                Log.d(TAG, "Starting logout async task '$jsessionid'")
                semaphore.acquire()
                LogOutAsyncTask(jsessionid, null, semaphore).execute()
            }
        }
        semaphore.acquire()
        super.onTaskRemoved(rootIntent)
        Log.d(TAG, "onTaskRemoved()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "onLowMemory()")
    }

}