package com.steptracker.app.data

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StepPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("step_tracker_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_DATE = "last_date"
        private const val KEY_TODAY_STEPS = "today_steps"
        private const val KEY_SENSOR_BASE = "sensor_base"
        private const val KEY_LAST_SENSOR_VALUE = "last_sensor_value"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_STRIDE_LENGTH = "stride_length_meters"
        private const val KEY_WEIGHT_KG = "weight_kg"

        fun getTodayDateString(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }
    }

    var dailyGoal: Int
        get() = prefs.getInt(KEY_DAILY_GOAL, 10000)
        set(value) = prefs.edit().putInt(KEY_DAILY_GOAL, value).apply()

    var strideLengthMeters: Float
        get() = prefs.getFloat(KEY_STRIDE_LENGTH, 0.762f)
        set(value) = prefs.edit().putFloat(KEY_STRIDE_LENGTH, value).apply()

    var weightKg: Float
        get() = prefs.getFloat(KEY_WEIGHT_KG, 70.0f)
        set(value) = prefs.edit().putFloat(KEY_WEIGHT_KG, value).apply()

    var lastDate: String
        get() = prefs.getString(KEY_LAST_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_DATE, value).apply()

    var todaySteps: Int
        get() = prefs.getInt(KEY_TODAY_STEPS, 0)
        set(value) = prefs.edit().putInt(KEY_TODAY_STEPS, value).apply()

    var sensorBase: Float
        get() = prefs.getFloat(KEY_SENSOR_BASE, -1f)
        set(value) = prefs.edit().putFloat(KEY_SENSOR_BASE, value).apply()

    var lastSensorValue: Float
        get() = prefs.getFloat(KEY_LAST_SENSOR_VALUE, 0f)
        set(value) = prefs.edit().putFloat(KEY_LAST_SENSOR_VALUE, value).apply()

    fun calculateDistanceKm(steps: Int): Double {
        return (steps * strideLengthMeters) / 1000.0
    }

    fun calculateCalories(steps: Int): Double {
        // Average: ~0.04 kcal per step for a 70kg adult
        val factor = (weightKg / 70.0) * 0.04
        return steps * factor
    }

    fun calculateActiveMinutes(steps: Int): Int {
        // Average moderate walking cadence is ~100 steps per minute
        return steps / 100
    }
}
