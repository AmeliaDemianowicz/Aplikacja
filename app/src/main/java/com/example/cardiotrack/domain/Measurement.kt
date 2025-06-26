package com.example.cardiotrack.domain

import kotlinx.datetime.Instant
/**
 * Reprezentuje pojedynczy pomiar użytkownika, zawierający dane medyczne oraz unikalny identyfikator.
 *
 * @property id Unikalny identyfikator pomiaru.
 * @property data Dane pomiaru (tętno, ciśnienie, data, notatki).
 */
data class Measurement(
    val id: String,
    val data: MeasurementData
)
/**
 * Dane pojedynczego pomiaru ciśnienia krwi i tętna.
 *
 * @property bpm Liczba uderzeń serca na minutę (tętno).
 * @property sys Ciśnienie skurczowe .
 * @property dia Ciśnienie rozkurczowe .
 * @property date Data i czas wykonania pomiaru.
 * @property notes Dodatkowe notatki dotyczące pomiaru (opcjonalne).
 */
data class MeasurementData(
    val bpm: Int,
    val sys: Int,
    val dia: Int,
    val date: Instant,
    val notes: String? = null
)