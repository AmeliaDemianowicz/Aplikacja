package com.example.cardiotrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.WorkManager
import com.example.cardiotrack.ui.theme.CardioTrackTheme

class CardioTrackActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardioTrackTheme {
                CardioTrackApp()
            }
        }

        WorkManager.getInstance(applicationContext)
            .getWorkInfosByTagLiveData("measurement_notification")
            .observeForever { it ->
                it.forEach {
                    Log.i("WorkState", "Scheduled measurement notification $it")
                }
            }
    }
}

