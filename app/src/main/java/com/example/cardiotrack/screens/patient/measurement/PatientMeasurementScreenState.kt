package com.example.cardiotrack.screens.patient.measurement

import kotlinx.datetime.Instant
/**
 * Reprezentuje stan ekranu dodawania pomiaru pacjenta.
 *
 * @property bpm Wartość pulsu (HR) jako tekst.
 * @property bpmError Błąd walidacji wartości pulsu, jeśli występuje.
 * @property sys Wartość ciśnienia skurczowego (SYS) jako tekst.
 * @property sysError Błąd walidacji wartości ciśnienia skurczowego, jeśli występuje.
 * @property dia Wartość ciśnienia rozkurczowego (DIA) jako tekst.
 * @property diaError Błąd walidacji wartości ciśnienia rozkurczowego, jeśli występuje.
 * @property date Data i czas pomiaru jako [Instant], może być null, jeśli nie ustawiono.
 * @property dateError Błąd walidacji daty pomiaru, jeśli występuje.
 * @property showTimeModal Flaga określająca, czy wyświetlać modal wyboru czasu.
 * @property notes Dodatkowe notatki do pomiaru, może być null.
 * @property loading Flaga wskazująca, czy ekran jest w trakcie operacji ładowania/zapisu.
 */
data class PatientMeasurementScreenState(
    val bpm: String = "",
    val bpmError: String? = null,
    val sys: String = "",
    val sysError: String? = null,
    val dia: String = "",
    val diaError: String? = null,
    val date: Instant? = null,
    val dateError: String? = null,
    val showTimeModal: Boolean = false,
    val notes: String? = null,
    val loading: Boolean = false
)