package com.example.nasa_app.receivers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavDeepLinkBuilder
import com.example.nasa_app.R
import com.example.nasa_app.activities.main.MainActivity
import com.example.nasa_app.extensions.toDateString
import java.util.*
import android.media.RingtoneManager
import android.util.Log
import android.widget.Toast
import com.example.nasa_app.managers.NasaNotificationsManager
import com.example.nasa_app.utils.PreferencesHelper


class DailyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)
        showNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance)
            val notificationManager = getSystemService(context, NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

    }

    private fun showNotification(context: Context) {
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(context.getString(R.string.new_article))
            .setSound(sound)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.articleFragment)
            .setArguments(Bundle().apply {
                putString("date", Date().toDateString())
            })
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()

        builder.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(DAILY_NOTIFICATION_ID, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "DAILY_NOTIFICATION_CHANNEL"
        private const val DAILY_NOTIFICATION_ID = 1
    }
}