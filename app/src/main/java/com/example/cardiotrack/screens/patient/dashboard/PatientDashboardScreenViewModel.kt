package com.example.cardiotrack.screens.patient.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.screens.patient.measurement.PatientMeasurementScreen
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreen
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
/**
 * ViewModel dla ekranu pulpitu pacjenta.
 *
 * Zarządza stanem ekranu, ładowaniem pomiarów oraz nawigacją.
 *
 * @param routeData Dane przekazywane do ekranu, zawierające informacje o użytkowniku.
 * @param patientService Serwis odpowiedzialny za operacje związane z danymi pacjenta.
 * @param navController Kontroler nawigacji do przejść między ekranami.
 */
class PatientDashboardScreenViewModel(
    private val routeData: PatientDashboardScreen,
    private val patientService: PatientService,
    private val navController: NavController
) : ViewModel() {
    /** Aktualny stan ekranu przechowywany w MutableStateFlow. */
    val state = MutableStateFlow(PatientDashboardScreenState())

    init {
        loadMeasurements()
    }
    /**
     * Przełącza widok na poprzedni miesiąc i ustawia datę na pierwszy dzień tego miesiąca.
     */
    fun handleGoToPrevMonth() {
        state.update {
            val selectedMonth = it.selectedMonth.minusMonths(1)
            val selectedDate =
                it.selectedDate
                    .withYear(selectedMonth.year)
                    .withMonth(selectedMonth.monthValue)
                    .withDayOfMonth(1)

            it.copy(selectedMonth = selectedMonth, selectedDate = selectedDate)
        }
    }
    /**
     * Przełącza widok na następny miesiąc i ustawia datę na pierwszy dzień tego miesiąca.
     */
    fun handleGoToNextMonth() {
        state.update {
            val selectedMonth = it.selectedMonth.plusMonths(1)
            val selectedDate =
                it.selectedDate
                    .withYear(selectedMonth.year)
                    .withMonth(selectedMonth.monthValue)
                    .withDayOfMonth(1)

            it.copy(selectedMonth = selectedMonth, selectedDate = selectedDate)
        }
    }
    /**
     * Aktualizuje wybraną datę na ekranie.
     *
     * @param date Nowa data wybrana przez użytkownika.
     */
    fun handleSelectedDateChange(date: LocalDate) {
        state.update { it.copy(selectedDate = date) }
    }
    /**
     * Nawiguje do ekranu dodawania nowego pomiaru dla wybranego dnia.
     */
    fun addMeasurement() {
        navController.navigate(
            PatientMeasurementScreen(
                routeData.user,
                state.value.selectedDate.toKotlinLocalDate().toString()
            )
        )
    }
    /**
     * Usuwa podany pomiar i aktualizuje stan po usunięciu.
     *
     * @param measurement Pomiar do usunięcia.
     */
    fun deleteMeasurement(measurement: Measurement) {
        viewModelScope.launch {
            patientService.deleteMeasurement(measurement)
            state.update { it.copy(measurements = it.measurements.filter { it.id != measurement.id }) }
        }
    }
    /**
     * Ładuje pomiary pacjenta i aktualizuje stan.
     */
    fun loadMeasurements() {
        viewModelScope.launch {
            val measurements = patientService.getMeasurements(routeData.user)
            state.update { it.copy(measurements = measurements) }
        }
    }
    /**
     * Nawiguje do ekranu statystyk pacjenta.
     */
    fun redirectToStatistics() {
        navController.navigate(PatientStatisticsScreen(routeData.user))
    }
    /**
     * Sprawdza, czy średnie wartości pomiarów z danego dnia mieszczą się w referencyjnym zakresie.
     *
     * @param date Data do sprawdzenia.
     * @return `true` jeśli pomiary mieszczą się w zakresie, `false` w przeciwnym wypadku.
     */
    fun isDayInReferenceRange(date: LocalDate): Boolean {
        val dayMeasurements = state.value.measurements.filter {
            it.data.date.toLocalDateTime(TimeZone.currentSystemDefault()).date.toJavaLocalDate() == date
        }

        if (dayMeasurements.isEmpty()) return false

        val avgSys = dayMeasurements.map { it.data.sys }.average()
        val avgDia = dayMeasurements.map { it.data.dia }.average()
        val avgBpm = dayMeasurements.map { it.data.bpm }.average()

        return avgSys in 90.0..129.0 &&
                avgDia in 60.0..84.0 &&
                avgBpm in 60.0..100.0
    }
}
