package com.steptracker.app

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import com.steptracker.app.data.StepPreferences
import com.steptracker.app.receiver.WaterReminderReceiver
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
                var dailyGoal by remember { mutableIntStateOf(prefs.dailyGoal) }
                var waterConsumed by remember { mutableIntStateOf(prefs.waterConsumedMl) }
                val waterTarget = prefs.waterTargetMl
                val streakDays = prefs.streakDays
                val restDaysUsed = prefs.restDaysUsedThisWeek

                var weightKg by remember { mutableFloatStateOf(prefs.weightKg) }
                var heightCm by remember { mutableFloatStateOf(prefs.heightCm) }
                var weightGoal by remember { mutableStateOf(prefs.weightGoal) }
                var waterReminderInterval by remember { mutableIntStateOf(prefs.waterReminderIntervalHours) }

                var showFallbackSheet by remember { mutableStateOf(false) }

                val recentHistory by StepApplication.database.stepDao()
                    .getRecentRecords(7)
                    .collectAsState(initial = emptyList())

                val distanceKm = remember(currentSteps, prefs.strideLengthMeters) {
                    prefs.calculateDistanceKm(currentSteps)
                }
                val calories = remember(currentSteps, weightKg) {
                    prefs.calculateCalories(currentSteps)
                }
                val activeMins = remember(currentSteps) {
                    prefs.calculateActiveMinutes(currentSteps)
                }

                StepTrackerScreen(
                    currentSteps = currentSteps,
                    dailyGoal = dailyGoal,
                    distanceKm = distanceKm,
                    calories = calories,
                    activeMinutes = activeMins,
                    recentHistory = recentHistory,
                    waterConsumedMl = waterConsumed,
                    waterTargetMl = waterTarget,
                    waterReminderIntervalHours = waterReminderInterval,
                    streakDays = streakDays,
                    restDaysUsed = restDaysUsed,
                    weightKg = weightKg,
                    heightCm = heightCm,
                    weightGoal = weightGoal,
                    onUpdateGoal = { newGoal ->
                        prefs.dailyGoal = newGoal
                        dailyGoal = newGoal
                    },
                    onLogWater = { addedMl ->
                        val updated = waterConsumed + addedMl
                        prefs.waterConsumedMl = updated
                        waterConsumed = updated
                        Toast.makeText(this@MainActivity, "+${addedMl}ml water logged! Stay hydrated!", Toast.LENGTH_SHORT).show()
                    },
                    onResetWater = {
                        prefs.waterConsumedMl = 0
                        waterConsumed = 0
                        Toast.makeText(this@MainActivity, "Water reset to 0 ml for fresh tracking.", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateHealthProfile = { newWeight, newHeight, newGoal ->
                        prefs.weightKg = newWeight
                        prefs.heightCm = newHeight
                        prefs.weightGoal = newGoal
                        // Update stride length: ~0.415 of height in meters
                        prefs.strideLengthMeters = (newHeight * 0.415f) / 100f

                        weightKg = newWeight
                        heightCm = newHeight
                        weightGoal = newGoal
                        Toast.makeText(this@MainActivity, "Health Profile saved! Calorie goals updated.", Toast.LENGTH_SHORT).show()
                    },
                    onUpdateWaterReminderInterval = { hours ->
                        prefs.waterReminderIntervalHours = hours
                        waterReminderInterval = hours
                        WaterReminderReceiver.scheduleReminder(this@MainActivity, hours)
                        if (hours > 0) {
                            Toast.makeText(this@MainActivity, "Water reminder scheduled every $hours hour(s).", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Water reminder turned off.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onTriggerTestWaterReminder = {
                        WaterReminderReceiver.sendInstantNotification(this@MainActivity)
                        Toast.makeText(this@MainActivity, "Notification sent! Check your notification bar.", Toast.LENGTH_LONG).show()
                    },
                    onDonateClick = {
                        launchUpiPaymentChooser {
                            showFallbackSheet = true
                        }
                    },
                    onCopyUpiClick = {
                        copyUpiToClipboard()
                    },
                    onInstagramClick = {
                        openInstagramProfile()
                    },
                    showFallbackSheet = showFallbackSheet,
                    onDismissFallbackSheet = { showFallbackSheet = false }
                )
            }
        }
    }

    private fun launchUpiPaymentChooser(onFallback: () -> Unit) {
        try {
            val upiUri = Uri.parse("upi://pay?pa=jaynishad2010@okhdfcbank&pn=Vijay%20Kumar&aid=uGICAgIDQqveaLw")
            val intent = Intent(Intent.ACTION_VIEW, upiUri)
            val chooser = Intent.createChooser(intent, "Choose an app to pay with")
            startActivity(chooser)
        } catch (e: Exception) {
            onFallback()
        }
    }

    private fun copyUpiToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("UPI ID", "jaynishad2010@okhdfcbank")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "UPI ID copied.", Toast.LENGTH_SHORT).show()
    }

    private fun openInstagramProfile() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/momentswithvms/"))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open Instagram", Toast.LENGTH_SHORT).show()
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
