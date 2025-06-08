package com.example.cardiotrack.screens.patient.measurement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cardiotrack.domain.Sex
import com.example.cardiotrack.domain.User
import com.example.cardiotrack.services.patient.FirebasePatientService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Serializable
data class PatientMeasurementScreen(val user: User.Patient)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientMeasurementScreen(
    routeData: PatientMeasurementScreen,
    viewModel: PatientMeasurementScreenViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        OutlinedTextField(
            label = { Text("Puls (HR) [bpm]") },
            value = state.bpm ?: "",
            onValueChange = viewModel::handleBpmChange,
            enabled = !state.loading,
            isError = state.bpmError != null,
            supportingText = state.bpmError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            label = { Text("Ciśnienie skurczowe (SYS) [mmHg]") },
            value = state.sys ?: "",
            onValueChange = viewModel::handleSysChange,
            enabled = !state.loading,
            isError = state.sysError != null,
            supportingText = state.sysError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            label = { Text("Ciśnienie rozkurczowe (DIA) [mmHg]") },
            value = state.dia ?: "",
            onValueChange = viewModel::handleDiaChange,
            enabled = !state.loading,
            isError = state.diaError != null,
            supportingText = state.diaError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.date?.let {
                DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")
                    .format(it.toJavaInstant().atZone(ZoneId.systemDefault()))
            } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Data") },
            isError = state.dateError != null,
            supportingText = state.dateError?.let { { Text(it) } },
            trailingIcon = {
                IconButton(onClick = viewModel::showDateModal) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick Date")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused && !state.showDateModal) {
                        viewModel.showDateModal()
                    }
                }

        )
        if (state.showDateModal) {
            DatePickerDialog(
                onDismissRequest = {
                    focusManager.clearFocus()
                    viewModel.hideDateModal()
                },
                confirmButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        viewModel.handleDateChange(
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
        if (state.showTimeModal) {
            TimePickerDialog(
                title = { TimePickerDialogDefaults.Title(displayMode = TimePickerDisplayMode.Picker) },
                onDismissRequest = {
                    focusManager.clearFocus()
                    viewModel.hideTimeModal()
                },
                confirmButton = {
                    TextButton(onClick = {
                        focusManager.clearFocus()
                        viewModel.handleTimeChange(timePickerState.hour, timePickerState.minute)
                    }) {
                        Text("Potwierdź")
                    }
                }
            ) {
                TimePicker(state = timePickerState)
            }
        }
        OutlinedTextField(
            label = { Text("Dodatkowe notatki") },
            value = state.notes ?: "",
            onValueChange = viewModel::handleNotesChange,
            enabled = !state.loading,
            singleLine = false,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = { viewModel.addMeasurement(routeData.user) }
        ) {
            Text("Dodaj pomiar")
        }
    }
}


@Preview
@Composable
fun PatientMeasurementScreenPreview() {
    PatientMeasurementScreen(
        routeData = PatientMeasurementScreen(
            User.Patient(
                id = "",
                firstName = "",
                lastName = "",
                birthDate = Clock.System.now(),
                sex = Sex.MAN
            )
        ),
        viewModel = viewModel(factory = viewModelFactory {
            initializer { PatientMeasurementScreenViewModel(FirebasePatientService()) }
        })
    )
}