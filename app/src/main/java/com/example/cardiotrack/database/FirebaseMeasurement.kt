package com.example.cardiotrack.database

import com.example.cardiotrack.domain.Measurement
import kotlinx.datetime.Instant

data class FirebaseMeasurement(
    val userId: String? = null,
    val bpm: Int? = null,
    val sys: Int? = null,
    val dia: Int? = null,
    val date: Long? = null,
    val notes: String? = null,
) {
    companion object {
        fun deserialize(measurement: FirebaseMeasurement): Measurement {
            checkNotNull(measurement.bpm)
            checkNotNull(measurement.sys)
            checkNotNull(measurement.dia)
            checkNotNull(measurement.date)
            return Measurement(
                bpm = measurement.bpm,
                sys = measurement.sys,
                dia = measurement.dia,
                date = Instant.fromEpochMilliseconds(measurement.date),
                notes = measurement.notes
            )
        }
    }
}