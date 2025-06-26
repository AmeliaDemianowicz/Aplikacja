package com.example.cardiotrack

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.Duration
import java.time.LocalDateTime

/**
 * Worker odpowiedzialny za wyświetlanie powiadomień o konieczności dodania nowego pomiaru.
 *
 * Korzysta z Android WorkManager do wykonywania zadań w tle.
 *
 * @param context Kontekst aplikacji.
 * @param params Parametry pracy Workera.
 */
class CardioTrackNotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {
    /**
     * Wykonuje zadanie Workera - tworzy i wyświetla powiadomienie.
     *
     * Wymaga uprawnienia [Manifest.permission.POST_NOTIFICATIONS].
     *
     * @return Result.success() w przypadku pomyślnego wykonania.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        val channelId = "measurement_channel"
        val channel = NotificationChannel(
            channelId,
            "Measurement Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Dodaj pomiar!")
            .setContentText("Nadeszła pora aby dodać pomiar, przejdź do aplikacji")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        notificationManager.notify(1, notification)

        return Result.success()
    }

    companion object {
        /**
         * Oblicza opóźnienie (w ms) do najbliższego wystąpienia podanej godziny i minuty.
         *
         * Jeśli czas podany jest już za nami, liczy do tej godziny następnego dnia.
         *
         * @param hour Godzina (0-23)
         * @param minute Minuta (0-59)
         * @return Opóźnienie w milisekundach od teraz
         */
        fun initialDelay(hour: Int, minute: Int): Long {
            val now = LocalDateTime.now()
            val nextTime = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)

            val delay = if (nextTime.isAfter(now)) {
                Duration.between(now, nextTime)
            } else {
                Duration.between(now, nextTime.plusDays(1))
            }

            return delay.toMillis()
        }
    }
}
