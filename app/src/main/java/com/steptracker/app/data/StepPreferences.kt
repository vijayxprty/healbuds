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
        private const val KEY_HEIGHT_CM = "height_cm"
        private const val KEY_WEIGHT_GOAL = "weight_goal"
        private const val KEY_DIET_PREF = "diet_preference"
        private const val KEY_WATER_CONSUMED_ML = "water_consumed_ml"
        private const val KEY_WATER_TARGET_ML = "water_target_ml"
        private const val KEY_STREAK_DAYS = "streak_days"
        private const val KEY_REST_DAYS_USED = "rest_days_used"

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
        get() = prefs.getFloat(KEY_WEIGHT_KG, 68.0f)
        set(value) = prefs.edit().putFloat(KEY_WEIGHT_KG, value).apply()

    var heightCm: Float
        get() = prefs.getFloat(KEY_HEIGHT_CM, 172.0f)
        set(value) = prefs.edit().putFloat(KEY_HEIGHT_CM, value).apply()

    var weightGoal: String
        get() = prefs.getString(KEY_WEIGHT_GOAL, "maintain") ?: "maintain"
        set(value) = prefs.edit().putString(KEY_WEIGHT_GOAL, value).apply()

    var dietPreference: String
        get() = prefs.getString(KEY_DIET_PREF, "veg") ?: "veg"
        set(value) = prefs.edit().putString(KEY_DIET_PREF, value).apply()

    var waterConsumedMl: Int
        get() = prefs.getInt(KEY_WATER_CONSUMED_ML, 1500)
        set(value) = prefs.edit().putInt(KEY_WATER_CONSUMED_ML, value).apply()

    var waterTargetMl: Int
        get() = prefs.getInt(KEY_WATER_TARGET_ML, 2500)
        set(value) = prefs.edit().putInt(KEY_WATER_TARGET_ML, value).apply()

    var streakDays: Int
        get() = prefs.getInt(KEY_STREAK_DAYS, 12)
        set(value) = prefs.edit().putInt(KEY_STREAK_DAYS, value).apply()

    var restDaysUsedThisWeek: Int
        get() = prefs.getInt(KEY_REST_DAYS_USED, 1)
        set(value) = prefs.edit().putInt(KEY_REST_DAYS_USED, value).apply()

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
