package com.example.cardiotrack.screens.patient.statistics

import com.example.cardiotrack.domain.Measurement
import kotlinx.datetime.toJavaInstant
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class PatientAggregatedStatistics(val bpm: Int, val sys: Int, val dia: Int)

data class PatientStatisticsScreenState(
    val measurements: List<Measurement> = emptyList(),
    val loading: Boolean = false
) {
    fun weeklyAverage(): PatientAggregatedStatistics? {
        return average(Instant.now().minus(7, ChronoUnit.DAYS))
    }

    fun monthlyAverage(): PatientAggregatedStatistics? {
        return average(Instant.now().minus(30, ChronoUnit.DAYS))
    }

    fun daysInReferenceRange(): Int {
        return measurements.count { isInReferenceRange(it) }
    }

    fun daysNotInReferenceRange(): Int {
        return measurements.count { !isInReferenceRange(it) }
    }

    private fun isInReferenceRange(measurement: Measurement): Boolean {
        return measurement.bpm >= 60 &&
                measurement.bpm <= 100 &&
                measurement.sys >= 90 &&
                measurement.sys <= 129 &&
                measurement.dia >= 60 &&
                measurement.dia <= 84
    }

    private fun average(from: Instant): PatientAggregatedStatistics? {
        val matching = measurements.filter { it.date.toJavaInstant().isAfter(from) }

        if (matching.isEmpty()) {
            return null
        }

        return PatientAggregatedStatistics(
            bpm = matching.map { it.bpm }.average().roundToInt(),
            sys = matching.map { it.sys }.average().roundToInt(),
            dia = matching.map { it.dia }.average().roundToInt()
        )
    }
}