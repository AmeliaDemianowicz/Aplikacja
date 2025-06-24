package com.example.cardiotrack.database

import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import kotlinx.datetime.Instant

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