package com.example.cardiotrack.screens.auth.signup


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TimePickerDialogDefaults
import androidx.compose.material3.TimePickerDisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.example.cardiotrack.R
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.services.auth.FirebaseAuthService
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Opcje płci dostępne w formularzu rejestracji.
 */
val SEX_OPTIONS = listOf(Sex.MAN, Sex.WOMAN)
/**
 * Mapowanie wartości płci na etykiety widoczne dla użytkownika.
 */
val SEX_LABELS = mapOf(Sex.MAN to "Mężczyzna", Sex.WOMAN to "Kobieta")
/**
 * Obiekt reprezentujący ekran rejestracji.
 */
@Serializable
data object SignUpScreen
/**
 * Główny ekran rejestracji użytkownika. Wyświetla logo oraz odpowiedni etap formularza rejestracji.
 *
 * @param viewModel ViewModel zarządzający stanem formularza rejestracji.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        when (state.step) {
            SignUpScreenStep.CREDENTIALS -> SignUpScreenCredentialsStep(viewModel)
            SignUpScreenStep.PERSONAL_INFO -> SignUpScreenPersonalInfoStep(viewModel)
            SignUpScreenStep.MEASUREMENTS -> SignUpScreenMeasurementsStep(viewModel)
        }

    }
}
/**
 * Komponent wyświetlający pierwszy krok rejestracji – dane logowania.
 *
 * @param viewModel ViewModel dostarczający dane oraz obsługę logiki UI.
 */
@Composable
fun SignUpScreenCredentialsStep(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OutlinedTextField(
        label = { Text("E-mail") },
        value = state.email,
        onValueChange = viewModel::handleEmailChange,
        enabled = !state.loading,
        isError = state.emailError != null,
        supportingText = state.emailError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    )
    OutlinedTextField(
        label = { Text("Hasło") },
        value = state.password,
        onValueChange = viewModel::handlePasswordChange,
        enabled = !state.loading,
        isError = state.passwordError != null,
        supportingText = state.passwordError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        visualTransformation = {
            TransformedText(
                AnnotatedString("*".repeat(it.text.length)), OffsetMapping.Identity
            )
        },
    )
    OutlinedTextField(
        label = { Text("Powtórz hasło") },
        value = state.passwordRepeat,
        onValueChange = viewModel::handlePasswordRepeatChange,
        enabled = !state.loading,
        isError = state.passwordRepeatError != null,
        supportingText = state.passwordRepeatError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        visualTransformation = {
            TransformedText(
                AnnotatedString("*".repeat(it.text.length)), OffsetMapping.Identity
            )
        },
    )

    Spacer(modifier = Modifier.padding(20.dp))
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = viewModel::goToPersonalInfoStep
    ) {
        Text("Zarejestruj się")
    }
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = viewModel::handleSignIn
    ) {
        Text("Zaloguj się")
    }
}
/**
 * Komponent wyświetlający drugi krok rejestracji – informacje osobiste (imię, nazwisko, PESEL, płeć, data urodzenia).
 *
 * Obsługuje również wybór daty za pomocą dialogu i rozwijane menu wyboru płci.
 *
 * @param viewModel ViewModel zarządzający danymi użytkownika i kontrolą UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenPersonalInfoStep(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        label = { Text("Imie") },
        value = state.firstName,
        onValueChange = viewModel::handleFirstNameChange,
        enabled = !state.loading,
        isError = state.firstNameError != null,
        supportingText = state.firstNameError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    )
    OutlinedTextField(
        label = { Text("Nazwisko") },
        value = state.lastName,
        onValueChange = viewModel::handleLastNameChange,
        enabled = !state.loading,
        isError = state.lastNameError != null,
        supportingText = state.lastNameError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    )

    OutlinedTextField(
        label = { Text("PESEL") },
        value = state.pesel,
        onValueChange = viewModel::onPeselChanged,
        enabled = !state.loading,
        isError = state.peselError != null,
        supportingText = state.peselError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    )

    OutlinedTextField(
        value = state.birthDate?.let {
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .format(it.toJavaInstant().atZone(ZoneId.systemDefault()))
        } ?: "",
        onValueChange = {},
        enabled = !state.loading,
        readOnly = true,
        label = { Text("Data urodzenia") },
        isError = state.birthDateError != null,
        supportingText = state.birthDateError?.let { { Text(it) } },
        trailingIcon = {
            IconButton(onClick = viewModel::showBirthDateModal) {
                Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
            }
        },
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused && !state.showBirthDateModal) {
                    viewModel.showBirthDateModal()
                }
            }

    )
    if (state.showBirthDateModal) {
        DatePickerDialog(
            onDismissRequest = {
                focusManager.clearFocus()
                viewModel.hideBirthDateModal()
            },
            confirmButton = {
                TextButton(onClick = {
                    focusManager.clearFocus()
                    viewModel.handleBirthDateChange(
                        Instant.fromEpochMilliseconds(
                            datePickerState.selectedDateMillis ?: 0L
                        )
                    )
                }) {
                    Text("Potwierdź")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    ExposedDropdownMenuBox(
        expanded = state.showSexDropdown,
        onExpandedChange = viewModel::changeShowSexDropdown,
    ) {
        OutlinedTextField(
            value = state.sex?.let { SEX_LABELS[it] } ?: "",
            onValueChange = {},
            enabled = !state.loading,
            readOnly = true,
            label = { Text("Płeć") },
            isError = state.sexError != null,
            supportingText = state.sexError?.let { { Text(it) } },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = state.showSexDropdown,
            onDismissRequest = { viewModel.changeShowSexDropdown(false) }
        ) {
            SEX_OPTIONS.forEach { sex ->
                DropdownMenuItem(
                    text = { Text(SEX_LABELS[sex] ?: sex.name) },
                    onClick = {
                        viewModel.handleSexChange(sex)
                        viewModel.changeShowSexDropdown(false)
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.padding(20.dp))
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = viewModel::goToMeasurementsStep
    ) {
        Text("Dalej")
    }
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = viewModel::goBackToCredentialsStep
    ) {
        Text("Wróć")
    }
}

/**
 * Komponent wyświetlający trzeci krok rejestracji – wybór lekarza prowadzącego, ilości powiadomień i godzin pomiarów.
 *
 * Umożliwia użytkownikowi wybór liczby codziennych pomiarów oraz ustawienie ich godzin za pomocą TimePickerDialog.
 *
 * @param viewModel ViewModel zarządzający stanem i akcjami w tym kroku rejestracji.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreenMeasurementsStep(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ExposedDropdownMenuBox(
        expanded = state.showDoctorDropdown,
        onExpandedChange = viewModel::changeDoctorDropdown
    ) {
        OutlinedTextField(
            value = state.doctor?.let { "${it.firstName} ${it.lastName}" } ?: "",
            onValueChange = {},
            enabled = !state.loading,
            readOnly = true,
            label = { Text("Doktor") },
            isError = state.doctorError != null,
            supportingText = state.doctorError?.let { { Text(it) } },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = state.showDoctorDropdown,
            onDismissRequest = { viewModel.changeDoctorDropdown(false) }
        ) {
            state.availableDoctors.forEach { doctor ->
                DropdownMenuItem(
                    text = { Text("${doctor.firstName} ${doctor.lastName}") },
                    onClick = {
                        viewModel.handleDoctorChange(doctor)
                        viewModel.changeDoctorDropdown(false)
                    }
                )
            }
        }
    }

    ExposedDropdownMenuBox(
        expanded = state.showDailyMeasurementCountDropdown,
        onExpandedChange = viewModel::changeDailyMeasurementRemindersCountDropdown
    ) {
        OutlinedTextField(
            value = state.dailyMeasurementRemindersCount.toString(),
            onValueChange = {},
            enabled = !state.loading,
            readOnly = true,
            label = { Text("Ilość dziennych powiadomień o pomiarach") },
            isError = state.dailyMeasurementRemindersCountError != null,
            supportingText = state.dailyMeasurementRemindersCountError?.let { { Text(it) } },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = state.showDailyMeasurementCountDropdown,
            onDismissRequest = { viewModel.changeDailyMeasurementRemindersCountDropdown(false) }
        ) {
            listOf(1, 2, 3).forEach { count ->
                DropdownMenuItem(
                    text = { Text("$count") },
                    onClick = {
                        viewModel.handleDailyMeasurementRemindersCountChange(count)
                        viewModel.changeDailyMeasurementRemindersCountDropdown(false)
                    }
                )
            }
        }
    }

    (0..<state.dailyMeasurementRemindersCount).forEach { index ->
        val timePickerState = rememberTimePickerState()
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        OutlinedTextField(
            value = state.dailyMeasurementReminders[index]
                ?.format(DateTimeFormatter.ofPattern("HH:mm"))
                ?: "",
            onValueChange = {},
            enabled = !state.loading,
            readOnly = true,
            shape = RoundedCornerShape(15.dp),
            label = { Text("Godzina ${index + 1} powiadomienia") },
            isError = state.dailyMeasurementRemindersErrors[index] != null,
            supportingText = state.dailyMeasurementRemindersErrors[index]?.let { { Text(it) } },
            trailingIcon = {
                IconButton(onClick = { viewModel.showDailyMeasurementReminderTimeModal(index) }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused && !state.showDailyMeasurementRemindersTimeModals[index]) {
                        viewModel.showDailyMeasurementReminderTimeModal(index)
                    }
                }

        )
        if (state.showDailyMeasurementRemindersTimeModals[index]) {
            TimePickerDialog(
                title = { TimePickerDialogDefaults.Title(displayMode = TimePickerDisplayMode.Picker) },
                onDismissRequest = {
                    focusManager.clearFocus()
                    viewModel.hideDailyMeasurementRemindersTimeModals(index)
                },
                confirmButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        viewModel.handleDailyMeasurementReminderChange(
                            index,
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    }) {
                        Text("Potwierdź")
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
    }

    Spacer(modifier = Modifier.padding(20.dp))
    FilledTonalButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = { viewModel.handleSignUp(context) }
    ) {
        Text("Dalej")
    }
    TextButton(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        enabled = !state.loading,
        onClick = viewModel::goBackToPersonalInfoStep
    ) {
        Text("Wróć")
    }
}


@Preview
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    SignUpScreen(
        viewModel = viewModel(factory = viewModelFactory {
            initializer { SignUpScreenViewModel(FirebaseAuthService(), navController) }
        })
    )
}