package com.steptracker.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.steptracker.app.service.StepTrackerService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            StepTrackerService.startService(context)
        }
    }
}
