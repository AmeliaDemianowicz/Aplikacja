package com.example.cardiotrack.services.patient

import com.example.cardiotrack.domain.Measurement
import com.example.cardiotrack.domain.MeasurementData
import com.example.cardiotrack.domain.User
/**
 * Interfejs definiujący operacje związane z zarządzaniem pomiarami pacjenta.
 * Implementacje tego interfejsu odpowiadają za pobieranie, dodawanie i usuwanie danych pomiarowych.
 */
interface PatientService {
    /**
     * Pobiera listę wszystkich pomiarów danego pacjenta.
     *
     * @param user Obiekt pacjenta, dla którego mają zostać pobrane pomiary.
     * @return Lista pomiarów przypisana do pacjenta.
     */
    suspend fun getMeasurements(user: User.Patient): List<Measurement>
    /**
     * Dodaje nowy pomiar dla wskazanego pacjenta.
     *
     * @param user Pacjent, do którego przypisany jest nowy pomiar.
     * @param data Dane pomiarowe zawierające ciśnienie krwi, tętno, datę oraz notatki.
     */
    suspend fun addMeasurement(user: User.Patient, data: MeasurementData)
    /**
     * Usuwa wskazany pomiar z systemu.
     *
     * @param measurement Pomiar, który ma zostać usunięty.
     */
    suspend fun deleteMeasurement(measurement: Measurement)
}