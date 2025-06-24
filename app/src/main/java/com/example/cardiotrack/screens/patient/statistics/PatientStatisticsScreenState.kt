package com.example.cardiotrack.screens.patient.statistics

import com.example.cardiotrack.domain.Measurement
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
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
        return measurements
            .groupBy { it.data.date.toLocalDateTime(TimeZone.currentSystemDefault()).date }
            .count { (_, dailyMeasurements) ->
                val avgSys = dailyMeasurements.map { it.data.sys }.average()
                val avgDia = dailyMeasurements.map { it.data.dia }.average()
                val avgBpm = dailyMeasurements.map { it.data.bpm }.average()
                avgSys in 90.0..129.0 &&
                        avgDia in 60.0..84.0 &&
                        avgBpm in 60.0..100.0
            }
    }

    fun daysNotInReferenceRange(): Int {
        return measurements
            .groupBy { it.data.date.toLocalDateTime(TimeZone.currentSystemDefault()).date }
            .count { (_, dailyMeasurements) ->
                val avgSys = dailyMeasurements.map { it.data.sys }.average()
                val avgDia = dailyMeasurements.map { it.data.dia }.average()
                val avgBpm = dailyMeasurements.map { it.data.bpm }.average()
                avgSys !in 90.0..129.0 ||
                        avgDia !in 60.0..84.0 ||
                        avgBpm !in 60.0..100.0
            }
    }

    private fun average(from: Instant): PatientAggregatedStatistics? {
        val matching = measurements.filter { it.data.date.toJavaInstant().isAfter(from) }

        if (matching.isEmpty()) {
            return null
        }

        return PatientAggregatedStatistics(
            bpm = matching.map { it.data.bpm }.average().roundToInt(),
            sys = matching.map { it.data.sys }.average().roundToInt(),
            dia = matching.map { it.data.dia }.average().roundToInt()
        )
    }
}
