package com.steptracker.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.steptracker.app.data.DailyStepRecord
import com.steptracker.app.data.StepPreferences
import com.steptracker.app.service.StepTrackerService
import com.steptracker.app.ui.StepTrackerScreen
import com.steptracker.app.ui.theme.StepTrackerTheme

class MainActivity : ComponentActivity() {

    private lateinit var prefs: StepPreferences

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val activityRecognitionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        } else {
            true
        }

        if (activityRecognitionGranted) {
            StepTrackerService.startService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = StepPreferences(this)

        checkAndRequestPermissions()

        setContent {
            StepTrackerTheme {
                val currentSteps by StepTrackerService.currentStepsFlow.collectAsState()
                var dailyGoal by remember { mutableStateOf(prefs.dailyGoal) }

                val recentHistory by StepApplication.database.stepDao()
                    .getRecentRecords(7)
                    .collectAsState(initial = emptyList())

                val distanceKm = remember(currentSteps) { prefs.calculateDistanceKm(currentSteps) }
                val calories = remember(currentSteps) { prefs.calculateCalories(currentSteps) }
                val activeMins = remember(currentSteps) { prefs.calculateActiveMinutes(currentSteps) }

                StepTrackerScreen(
                    currentSteps = currentSteps,
                    dailyGoal = dailyGoal,
                    distanceKm = distanceKm,
                    calories = calories,
                    activeMinutes = activeMins,
                    recentHistory = recentHistory,
                    onUpdateGoal = { newGoal ->
                        prefs.dailyGoal = newGoal
                        dailyGoal = newGoal
                    }
                )
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            StepTrackerService.startService(this)
        }
    }
}
