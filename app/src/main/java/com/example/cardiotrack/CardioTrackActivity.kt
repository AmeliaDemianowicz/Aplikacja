package com.example.cardiotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cardiotrack.ui.theme.CardioTrackTheme
import java.util.concurrent.TimeUnit

class CardioTrackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleNotifications()
        enableEdgeToEdge()
        setContent {
            CardioTrackTheme {
                CardioTrackApp()
            }
        }
    }

    private fun scheduleNotifications() {
        val workRequest =
            PeriodicWorkRequestBuilder<CardioTrackNotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(
                    CardioTrackNotificationWorker.initialDelay(),
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "daily_notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}

