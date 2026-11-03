package com.steptracker.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.steptracker.app.MainActivity
import com.steptracker.app.R
import com.steptracker.app.StepApplication
import com.steptracker.app.data.DailyStepRecord
import com.steptracker.app.data.StepPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class StepTrackerService : Service(), SensorEventListener {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private lateinit var prefs: StepPreferences

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"

        private val _currentStepsFlow = MutableStateFlow(0)
        val currentStepsFlow = _currentStepsFlow.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, StepTrackerService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, StepTrackerService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }

        fun setStepsForTesting(context: Context, newCount: Int) {
            _currentStepsFlow.value = newCount
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = StepPreferences(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        // Initialize today's steps in flow
        checkDateRollover()
        _currentStepsFlow.value = prefs.todaySteps
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        startInForeground()
        registerSensors()

        return START_STICKY
    }

    private fun startInForeground() {
        val notification = createNotification(prefs.todaySteps, prefs.dailyGoal)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                } else {
                    0
                }
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun registerSensors() {
        stepCounterSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            // Fallback to step detector if hardware step counter is absent
            stepDetectorSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        checkDateRollover()

        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                val totalSensorSteps = event.values[0]

                if (prefs.sensorBase < 0 || totalSensorSteps < prefs.lastSensorValue) {
                    // Initial boot, or device rebooted
                    prefs.sensorBase = totalSensorSteps
                    prefs.todaySteps = 0
                }

                val stepsSinceBase = (totalSensorSteps - prefs.sensorBase).toInt()
                val calculatedSteps = if (stepsSinceBase >= 0) stepsSinceBase else 0
                prefs.todaySteps = calculatedSteps
                prefs.lastSensorValue = totalSensorSteps

                updateSteps(calculatedSteps)
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                // Secondary fallback
                if (stepCounterSensor == null) {
                    val newCount = prefs.todaySteps + 1
                    prefs.todaySteps = newCount
                    updateSteps(newCount)
                }
            }
        }
    }

    private fun checkDateRollover() {
        val currentDate = StepPreferences.getTodayDateString()
        val lastDate = prefs.lastDate

        if (lastDate.isEmpty()) {
            prefs.lastDate = currentDate
        } else if (lastDate != currentDate) {
            // Day rollover occurred! Save previous day's steps to Room database
            val previousDaySteps = prefs.todaySteps
            val goal = prefs.dailyGoal
            val dist = prefs.calculateDistanceKm(previousDaySteps)
            val cal = prefs.calculateCalories(previousDaySteps)
            val mins = prefs.calculateActiveMinutes(previousDaySteps)

            serviceScope.launch {
                StepApplication.database.stepDao().insertOrUpdate(
                    DailyStepRecord(
                        date = lastDate,
                        steps = previousDaySteps,
                        goal = goal,
                        distanceKm = dist,
                        calories = cal,
                        activeMinutes = mins
                    )
                )
            }

            // Reset for the new day
            prefs.lastDate = currentDate
            prefs.todaySteps = 0
            if (prefs.lastSensorValue > 0) {
                prefs.sensorBase = prefs.lastSensorValue
            }
            _currentStepsFlow.value = 0
        }
    }

    private fun updateSteps(steps: Int) {
        _currentStepsFlow.value = steps

        // Update database in background
        serviceScope.launch {
            val currentDate = StepPreferences.getTodayDateString()
            val goal = prefs.dailyGoal
            val dist = prefs.calculateDistanceKm(steps)
            val cal = prefs.calculateCalories(steps)
            val mins = prefs.calculateActiveMinutes(steps)

            StepApplication.database.stepDao().insertOrUpdate(
                DailyStepRecord(
                    date = currentDate,
                    steps = steps,
                    goal = goal,
                    distanceKm = dist,
                    calories = cal,
                    activeMinutes = mins
                )
            )
        }

        // Update foreground notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification(steps, prefs.dailyGoal))
    }

    private fun createNotification(steps: Int, goal: Int): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val remaining = (goal - steps).coerceAtLeast(0)
        val motivationalText = if (steps >= goal) {
            String.format(
                Locale.getDefault(),
                "🎉 Goal Crushed! %,d steps completed today! VMS Buddy is dancing with joy!",
                steps
            )
        } else {
            String.format(
                Locale.getDefault(),
                "\"%,d steps crushed! Only %,d more to hit our daily goal. Let's walk together!\"",
                steps,
                remaining
            )
        }

        return NotificationCompat.Builder(this, StepApplication.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Healbuds · VMS Buddy 👟")
            .setContentText(motivationalText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(motivationalText))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setProgress(goal, steps, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceJob.cancel()
    }
}
