package com.steptracker.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.steptracker.app.data.StepDatabase

class StepApplication : Application() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "step_counter_channel"
        const val WATER_NOTIFICATION_CHANNEL_ID = "water_reminders_channel"
        lateinit var database: StepDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = StepDatabase.getDatabase(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Step Counter Channel (Low importance ongoing)
            val stepName = getString(R.string.notification_channel_name)
            val stepDesc = getString(R.string.notification_channel_description)
            val stepChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, stepName, NotificationManager.IMPORTANCE_LOW).apply {
                description = stepDesc
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(stepChannel)

            // Water Reminder Channel (Default importance with alert sound & vibration)
            val waterName = "Water Drink Reminders"
            val waterDesc = "Actionable hydration reminders with quick log buttons"
            val waterChannel = NotificationChannel(WATER_NOTIFICATION_CHANNEL_ID, waterName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = waterDesc
                setShowBadge(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(waterChannel)
        }
    }
}
