package com.example.cardiotrack.screens.doctor.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.screens.patient.statistics.PatientStatisticsScreen
import com.example.cardiotrack.services.doctor.DoctorService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val PAGE_SIZE = 25
/**
 * ViewModel zarządzający stanem i logiką ekranu dashboardu lekarza.
 *
 * @param routeData Dane przekazane do ekranu, zawierające informacje o aktualnym lekarzu.
 * @param doctorService Serwis dostarczający dane związane z lekarzem i pacjentami.
 * @param navController Kontroler nawigacji służący do przechodzenia między ekranami.
 */
class DoctorDashboardScreenViewModel(
    private val routeData: DoctorDashboardScreen,
    private val doctorService: DoctorService,
    private val navController: NavController
) : ViewModel() {
    /** Aktualny stan ekranu dashboardu lekarza. */
    val state = MutableStateFlow(DoctorDashboardScreenState())

    init {
        loadNextPatientsPage()
    }
    /**
     * Obsługuje zmianę frazy wyszukiwania pacjenta.
     * Resetuje listę pacjentów oraz stan paginacji i inicjuje ponowne ładowanie danych.
     *
     * @param searchTerm Nowa fraza wyszukiwania.
     */
    fun handleSearchTermChange(searchTerm: String) {
        state.update {
            it.copy(
                fullNameSearchTerm = searchTerm,
                patients = emptyList(),
                cursor = null,
                hasMore = true
            )
        }
        loadNextPatientsPage()
    }
    /**
     * Ładuje kolejną stronę pacjentów na podstawie obecnego stanu wyszukiwania i paginacji.
     * Ignoruje wywołanie, jeśli już trwa ładowanie lub brak jest więcej danych.
     */
    fun loadNextPatientsPage() {
        if (state.value.loading || !state.value.hasMore) {
            return
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, error -> handleError(error) }) {
            state.update { it.copy(loading = true) }

            val (patients, cursor, hasMore) = doctorService.getPatientsByName(
                doctor = routeData.user,
                fullName = state.value.fullNameSearchTerm,
                cursor = state.value.cursor,
                limit = PAGE_SIZE.toLong()
            )

            state.update {
                it.copy(
                    loading = false,
                    patients = it.patients + patients,
                    cursor = cursor,
                    hasMore = hasMore
                )
            }
        }
    }
    /**
     * Przekierowuje na ekran statystyk konkretnego pacjenta.
     *
     * @param user Pacjent, którego statystyki mają zostać wyświetlone.
     */
    fun redirectToStatistics(user: User.Patient) {
        navController.navigate(PatientStatisticsScreen(user))
    }
    /**
     * Obsługuje błędy podczas ładowania danych.
     * Aktualnie ustawia flagę ładowania na false, w przyszłości może wyświetlać komunikaty.
     *
     * @param error Błąd, który wystąpił.
     */
    private fun handleError(error: Throwable) {
        state.update { it.copy(loading = false) }
        // TODO: show error toast
    }
}