package com.example.cardiotrack.screens.patient.statistics

import com.example.cardiotrack.domain.Measurement
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
/**
 * Zestaw uśrednionych statystyk pacjenta.
 *
 * @property bpm Średnia liczba uderzeń serca na minutę.
 * @property sys Średnia wartość ciśnienia skurczowego (SYS) w mmHg.
 * @property dia Średnia wartość ciśnienia rozkurczowego (DIA) w mmHg.
 */
data class PatientAggregatedStatistics(val bpm: Int, val sys: Int, val dia: Int)
/**
 * Stan ekranu statystyk pacjenta zawierający listę pomiarów oraz flagę ładowania.
 *
 * @property measurements Lista pomiarów pacjenta.
 * @property loading Flaga określająca, czy dane są w trakcie ładowania.
 */
data class PatientStatisticsScreenState(
    val measurements: List<Measurement> = emptyList(),
    val loading: Boolean = false
) {
    /**
     * Oblicza średnie wartości pomiarów z ostatnich 7 dni.
     *
     * @return Uśrednione statystyki lub null, jeśli brak danych.
     */
    fun weeklyAverage(): PatientAggregatedStatistics? {
        return average(Instant.now().minus(7, ChronoUnit.DAYS))
    }
    /**
     * Oblicza średnie wartości pomiarów z ostatnich 30 dni.
     *
     * @return Uśrednione statystyki lub null, jeśli brak danych.
     */
    fun monthlyAverage(): PatientAggregatedStatistics? {
        return average(Instant.now().minus(30, ChronoUnit.DAYS))
    }
    /**
     * Liczy liczbę dni, w których średnie pomiary mieszczą się w zakresie referencyjnym.
     *
     * Zakresy referencyjne:
     * - SYS: 90–129 mmHg
     * - DIA: 60–84 mmHg
     * - HR: 60–100 uderzeń/min (w spoczynku)
     *
     * @return Liczba dni z pomiarami w normie.
     */
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
    /**
     * Liczy liczbę dni, w których średnie pomiary wychodzą poza zakres referencyjny.
     *
     * Zakresy referencyjne:
     * - SYS: 90–129 mmHg
     * - DIA: 60–84 mmHg
     * - HR: 60–100 uderzeń/min (w spoczynku)
     *
     * @return Liczba dni z pomiarami poza normą.
     */
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
    /**
     * Pomocnicza funkcja do wyliczenia średnich wartości pomiarów od określonej daty.
     *
     * @param from Data początkowa (pomiar musi być późniejszy niż ta data).
     * @return Uśrednione statystyki lub null, jeśli brak pasujących pomiarów.
     */
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
