package com.example.cardiotrack.services.doctor

import com.example.cardiotrack.domain.User
import com.google.firebase.firestore.DocumentSnapshot
/**
 * Reprezentuje stronę wyników pacjentów z paginacją.
 *
 * @property data Lista pacjentów.
 * @property cursor Dokument, który może być użyty jako wskaźnik do następnej strony danych.
 * @property hasMore Czy istnieją kolejne dane do pobrania.
 */
data class PatientsPage(
    val data: List<User.Patient>,
    val cursor: DocumentSnapshot?,
    val hasMore: Boolean
)
/**
 * Interfejs definiujący operacje, które mogą być wykonywane przez lekarza,
 * związane z zarządzaniem listą pacjentów.
 */
interface DoctorService {
    /**
     * Pobiera listę pacjentów przypisanych do lekarza, opcjonalnie filtrując po imieniu i nazwisku.
     * Wyniki są stronicowane przy użyciu kursorów Firestore.
     *
     * @param doctor Obiekt [User.Doctor] reprezentujący zalogowanego lekarza.
     * @param fullName Fragment lub pełne imię i nazwisko pacjenta (domyślnie pusty string).
     * @param cursor Kursor (ostatni dokument z poprzedniego zapytania) do paginacji, może być `null`.
     * @param limit Maksymalna liczba pacjentów do pobrania (domyślnie 20).
     *
     * @return [PatientsPage] zawierająca listę pacjentów, kursor i informację o dostępności kolejnej strony.
     */
    suspend fun getPatientsByName(
        doctor: User.Doctor,
        fullName: String = "",
        cursor: DocumentSnapshot? = null,
        limit: Long = 20
    ): PatientsPage
}