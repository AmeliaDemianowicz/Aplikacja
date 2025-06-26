package com.example.cardiotrack.screens.doctor.dashboard

import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.DocumentSnapshot
/**
 * Reprezentuje stan ekranu dashboardu lekarza.
 *
 * @property fullNameSearchTerm Aktualna fraza wyszukiwania pacjenta po imieniu i nazwisku.
 * @property patients Lista pacjentów wyświetlanych na ekranie.
 * @property cursor Kursor Firestore służący do paginacji danych (ostatni pobrany dokument).
 * @property loading Flaga wskazująca, czy trwa ładowanie danych.
 * @property hasMore Flaga informująca, czy są jeszcze dostępne kolejne strony pacjentów do załadowania.
 */
data class DoctorDashboardScreenState(
    val fullNameSearchTerm: String = "",
    val patients: List<User.Patient> = emptyList(),
    val cursor: DocumentSnapshot? = null,
    val loading: Boolean = false,
    val hasMore: Boolean = true
)