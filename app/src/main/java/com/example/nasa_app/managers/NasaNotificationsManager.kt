package com.example.nasa_app.managers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.nasa_app.receivers.DailyNotificationReceiver
import com.example.nasa_app.utils.PreferencesHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*

class NasaNotificationsManager(
    @ApplicationContext private val applicationContext: Context,
    private val preferencesHelper: PreferencesHelper
) {

    private val alarmManager =
        applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun setupDailyNotification(hourAndMinute: Pair<Int, Int>?) {
        preferencesHelper.dailyNotificationTime = hourAndMinute
        if (hourAndMinute != null) {
            alarmManager?.let {
                val intent = Intent(applicationContext, DailyNotificationReceiver::class.java)
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.SECOND, 0)
                    set(Calendar.HOUR_OF_DAY, hourAndMinute.first)
                    set(Calendar.MINUTE, hourAndMinute.second)
                }
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        applicationContext,
                        DAILY_NOTIFICATION_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager?.let {
                val intent = Intent(applicationContext, DailyNotificationReceiver::class.java)
                val pendingIntent =
                    PendingIntent.getBroadcast(
                        applicationContext,
                        DAILY_NOTIFICATION_REQUEST_CODE,
                        intent,
                        0
                    )
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    companion object {
        private const val DAILY_NOTIFICATION_REQUEST_CODE = 1
    }
}