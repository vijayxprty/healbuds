package com.steptracker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStepRecord(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val steps: Int,
    val goal: Int,
    val distanceKm: Double,
    val calories: Double,
    val activeMinutes: Int,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)
