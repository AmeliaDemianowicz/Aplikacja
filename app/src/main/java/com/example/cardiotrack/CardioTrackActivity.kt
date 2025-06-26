package com.example.cardiotrack

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.WorkManager
import com.example.cardiotrack.ui.theme.CardioTrackTheme
/**
 * Główna aktywność aplikacji CardioTrack.
 *
 * Odpowiada za zainicjalizowanie interfejsu użytkownika oraz monitorowanie
 * zaplanowanych zadań powiązanych z przypomnieniami o pomiarach.
 */
class CardioTrackActivity : ComponentActivity() {
    /**
     * Metoda wywoływana podczas tworzenia aktywności.
     * Ustawia motyw aplikacji, włącza tryb edge-to-edge oraz inicjalizuje
     * obserwację zaplanowanych zadań WorkManagera.
     *
     * @param savedInstanceState Stan zapisany podczas wcześniejszego działania (jeśli istnieje).
     */
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

