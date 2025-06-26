package com.example.cardiotrack.screens.auth.signin
/**
 * Reprezentuje stan ekranu logowania w aplikacji.
 *
 * Przechowuje informacje potrzebne do poprawnego działania interfejsu użytkownika,
 * takie jak wpisany e-mail, hasło, błędy walidacji oraz status ładowania.
 *
 * @property email Wpisany przez użytkownika adres e-mail.
 * @property emailError Komunikat błędu związany z e-mailem (np. nieprawidłowy format).
 * @property password Wpisane przez użytkownika hasło.
 * @property passwordError Komunikat błędu związany z hasłem (np. zbyt krótkie).
 * @property loading Flaga określająca, czy trwa operacja logowania (np. wysyłanie zapytania).
 */
data class SignInScreenState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val loading: Boolean = false,
)