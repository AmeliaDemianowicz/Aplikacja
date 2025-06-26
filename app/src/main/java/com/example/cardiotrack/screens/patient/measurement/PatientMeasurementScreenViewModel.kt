package com.example.cardiotrack.screens.patient.measurement

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.patient.dashboard.PatientDashboardScreen
import com.example.cardiotrack.services.patient.PatientService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
/**
 * ViewModel zarządzający stanem ekranu dodawania pomiaru pacjenta.
 *
 * @property patientService Serwis odpowiedzialny za operacje na danych pacjenta.
 * @property navController Kontroler nawigacji do zarządzania przejściami między ekranami.
 */
class PatientMeasurementScreenViewModel(
    private val patientService: PatientService,
    private val navController: NavController
) : ViewModel() {
    /**
     * Aktualny stan ekranu, przechowywany jako [MutableStateFlow].
     */
    val state = MutableStateFlow(PatientMeasurementScreenState())
    /**
     * Obsługuje zmianę wartości pulsu (BPM).
     * Akceptuje tylko cyfry, ustawia wartość i czyści błąd.
     *
     * @param bpm Nowa wartość pulsu jako tekst.
     */
    fun handleBpmChange(bpm: String) {
        if (bpm.isDigitsOnly()) {
            state.update { it.copy(bpm = bpm, bpmError = null) }
        }
    }
    /**
     * Obsługuje zmianę wartości ciśnienia skurczowego (SYS).
     * Akceptuje tylko cyfry, ustawia wartość i czyści błąd.
     *
     * @param sys Nowa wartość ciśnienia skurczowego jako tekst.
     */
    fun handleSysChange(sys: String) {
        if (sys.isDigitsOnly()) {
            state.update { it.copy(sys = sys, sysError = null) }
        }
    }
    /**
     * Obsługuje zmianę wartości ciśnienia rozkurczowego (DIA).
     * Akceptuje tylko cyfry, ustawia wartość i czyści błąd.
     *
     * @param dia Nowa wartość ciśnienia rozkurczowego jako tekst.
     */
    fun handleDiaChange(dia: String) {
        if (dia.isDigitsOnly()) {
            state.update { it.copy(dia = dia, diaError = null) }
        }
    }
    /**
     * Obsługuje zmianę wybranego czasu pomiaru.
     * Ustawia datę i czas na podstawie przekazanych wartości.
     *
     * @param date Data pomiaru.
     * @param hour Godzina pomiaru.
     * @param minute Minuta pomiaru.
     */
    fun handleTimeChange(date: LocalDate, hour: Int, minute: Int) {
        state.update {
            it.copy(
                date = date.atTime(hour, minute).toInstant(TimeZone.currentSystemDefault()),
                dateError = null
            )
        }
        hideTimeModal()
    }
    /**
     * Pokazuje modal do wyboru czasu.
     */
    fun showTimeModal() {
        state.update { it.copy(showTimeModal = true) }
    }
    /**
     * Ukrywa modal do wyboru czasu.
     */
    fun hideTimeModal() {
        state.update { it.copy(showTimeModal = false) }
    }
    /**
     * Obsługuje zmianę dodatkowych notatek do pomiaru.
     *
     * @param notes Nowa wartość notatek.
     */
    fun handleNotesChange(notes: String) {
        state.update { it.copy(notes = notes) }
    }
    /**
     * Dodaje nowy pomiar dla pacjenta, jeśli dane są poprawne.
     * Po dodaniu przekierowuje na ekran dashboardu pacjenta.
     *
     * @param user Obiekt pacjenta, dla którego dodajemy pomiar.
     */
    fun addMeasurement(user: User.Patient) {
        viewModelScope.launch {
            validateForm()
            val measurementData = state.value
            if (measurementData.date != null) {
                patientService.addMeasurement(
                    user,
                    MeasurementData(
                        bpm = measurementData.bpm.toInt(),
                        sys = measurementData.sys.toInt(),
                        dia = measurementData.dia.toInt(),
                        date = measurementData.date,
                        notes = measurementData.notes
                    )
                )
                navController.navigate(PatientDashboardScreen(user))
            }
        }
    }
    /**
     * Waliduje wszystkie pola formularza pomiaru.
     */
    private fun validateForm() {
        validateBpmField()
        validateSysField()
        validateDiaField()
        validateDateField()
    }
    /**
     * Waliduje pole wartości pulsu.
     */
    private fun validateBpmField() {
        if (state.value.bpm.isBlank()) {
            return state.update { it.copy(bpmError = "Puls nie może być pusty") }
        }
        if (state.value.bpm.toIntOrNull() == null) {
            return state.update { it.copy(bpmError = "Nieprawidłowy puls") }
        }
    }
    /**
     * Waliduje pole wartości ciśnienia skurczowego.
     */
    private fun validateSysField() {
        if (state.value.sys.isBlank()) {
            return state.update { it.copy(sysError = "Ciśnienie nie może być puste") }
        }
        if (state.value.sys.toIntOrNull() == null) {
            return state.update { it.copy(sysError = "Nieprawidłowe ciśnienie") }
        }
    }
    /**
     * Waliduje pole wartości ciśnienia rozkurczowego.
     */
    private fun validateDiaField() {
        if (state.value.dia.isBlank()) {
            return state.update { it.copy(diaError = "Ciśnienie nie może być puste") }
        }
        if (state.value.dia.toIntOrNull() == null) {
            return state.update { it.copy(diaError = "Nieprawidłowe ciśnienie") }
        }
    }
    /**
     * Waliduje pole daty pomiaru.
     */
    private fun validateDateField() {
        if (state.value.date == null) {
            return state.update { it.copy(dateError = "Data nie może być pusta") }
        }
    }
}