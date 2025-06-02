package com.example.cardiotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    }
}

