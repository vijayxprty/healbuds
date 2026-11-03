package com.steptracker.app.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.steptracker.app.MainActivity
import com.steptracker.app.R
import com.steptracker.app.StepApplication
import com.steptracker.app.data.StepPreferences
import java.util.Locale

class WaterReminderReceiver : BroadcastReceiver() {

    companion object {
        const val WATER_NOTIFICATION_ID = 2002

        const val ACTION_TRIGGER_WATER_REMINDER = "com.steptracker.app.TRIGGER_WATER_REMINDER"
        const val ACTION_LOG_WATER_250 = "com.steptracker.app.LOG_WATER_250"
        const val ACTION_LOG_WATER_500 = "com.steptracker.app.LOG_WATER_500"
        const val ACTION_SKIP = "com.steptracker.app.SKIP_WATER"

        fun scheduleReminder(context: Context, intervalHours: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_TRIGGER_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (intervalHours <= 0) {
                alarmManager.cancel(pendingIntent)
                return
            }

            val intervalMillis = intervalHours.toLong() * 60L * 60L * 1000L
            val triggerAtMillis = SystemClock.elapsedRealtime() + intervalMillis

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun sendInstantNotification(context: Context) {
            showNotification(context)
        }

        fun showNotification(context: Context) {
            val prefs = StepPreferences(context)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return

            // Open app on body tap
            val contentIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                201,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action 1: +250ml (1 Glass)
            val intent250 = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_LOG_WATER_250
            }
            val pending250 = PendingIntent.getBroadcast(
                context,
                202,
                intent250,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action 2: +500ml (Bottle)
            val intent500 = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_LOG_WATER_500
            }
            val pending500 = PendingIntent.getBroadcast(
                context,
                203,
                intent500,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Action 3: Skip
            val intentSkip = Intent(context, WaterReminderReceiver::class.java).apply {
                action = ACTION_SKIP
            }
            val pendingSkip = PendingIntent.getBroadcast(
                context,
                204,
                intentSkip,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val body = String.format(
                Locale.getDefault(),
                "Time for hydration! Currently at %,d / %,d ml. Drink water to stay energized!",
                prefs.waterConsumedMl,
                prefs.waterTargetMl
            )

            val notification = NotificationCompat.Builder(context, StepApplication.WATER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("💧 Time to Drink Water! (Healbuds)")
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_launcher_foreground, "🥛 1 Glass (+250ml)", pending250)
                .addAction(R.drawable.ic_launcher_foreground, "🍼 +500ml", pending500)
                .addAction(R.drawable.ic_launcher_foreground, "Skip", pendingSkip)
                .build()

            notificationManager.notify(WATER_NOTIFICATION_ID, notification)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = StepPreferences(context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        when (intent?.action) {
            ACTION_TRIGGER_WATER_REMINDER -> {
                showNotification(context)
                // Schedule next alarm
                val interval = prefs.waterReminderIntervalHours
                if (interval > 0) {
                    scheduleReminder(context, interval)
                }
            }
            ACTION_LOG_WATER_250 -> {
                val newAmount = prefs.waterConsumedMl + 250
                prefs.waterConsumedMl = newAmount
                notificationManager?.cancel(WATER_NOTIFICATION_ID)
                Toast.makeText(context, "🥛 +250ml logged! Total: ${newAmount}ml", Toast.LENGTH_SHORT).show()
                val interval = prefs.waterReminderIntervalHours
                if (interval > 0) scheduleReminder(context, interval)
            }
            ACTION_LOG_WATER_500 -> {
                val newAmount = prefs.waterConsumedMl + 500
                prefs.waterConsumedMl = newAmount
                notificationManager?.cancel(WATER_NOTIFICATION_ID)
                Toast.makeText(context, "🍼 +500ml logged! Total: ${newAmount}ml", Toast.LENGTH_SHORT).show()
                val interval = prefs.waterReminderIntervalHours
                if (interval > 0) scheduleReminder(context, interval)
            }
            ACTION_SKIP -> {
                notificationManager?.cancel(WATER_NOTIFICATION_ID)
                Toast.makeText(context, "Reminder skipped. Stay hydrated!", Toast.LENGTH_SHORT).show()
                val interval = prefs.waterReminderIntervalHours
                if (interval > 0) scheduleReminder(context, interval)
            }
        }
    }
}
