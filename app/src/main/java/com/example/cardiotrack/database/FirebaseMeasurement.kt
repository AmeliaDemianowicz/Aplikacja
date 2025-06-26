package com.example.cardiotrack.database

import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import kotlinx.datetime.Instant
/**
 * Reprezentacja pomiaru ciśnienia krwi i tętna zapisywanego w bazie Firebase.
 *
 * @property id Identyfikator pomiaru.
 * @property userId Identyfikator użytkownika, do którego należy pomiar.
 * @property bpm Liczba uderzeń serca na minutę (tętno).
 * @property sys Ciśnienie skurczowe.
 * @property dia Ciśnienie rozkurczowe.
 * @property date Data wykonania pomiaru .
 * @property notes Notatki dodatkowe do pomiaru.
 */
data class FirebaseMeasurement(
    val id: String? = null,
    val userId: String? = null,
    val bpm: Int? = null,
    val sys: Int? = null,
    val dia: Int? = null,
    val date: Long? = null,
    val notes: String? = null,
) {
    companion object {
        /**
         * Deserializuje obiekt [FirebaseMeasurement] do obiektu domenowego [Measurement].
         *
         * @param measurement Pomiar w formacie Firebase.
         * @return Obiekt [Measurement] z odpowiednimi danymi.
         * @throws IllegalStateException jeśli którekolwiek z wymaganych pól jest niezdefiniowane.
         */
        fun deserialize(measurement: FirebaseMeasurement): Measurement {
            checkNotNull(measurement.id)
            checkNotNull(measurement.bpm)
            checkNotNull(measurement.sys)
            checkNotNull(measurement.dia)
            checkNotNull(measurement.date)
            return Measurement(
                id = measurement.id,
                data = MeasurementData(
                    bpm = measurement.bpm,
                    sys = measurement.sys,
                    dia = measurement.dia,
                    date = Instant.fromEpochMilliseconds(measurement.date),
                    notes = measurement.notes
                )
            )
        }
    }
}