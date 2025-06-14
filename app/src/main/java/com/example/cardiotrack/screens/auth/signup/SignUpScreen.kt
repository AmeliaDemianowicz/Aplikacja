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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
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


val SEX_OPTIONS = listOf(Sex.MAN, Sex.WOMAN)
val SEX_LABELS = mapOf(Sex.MAN to "Mężczyzna", Sex.WOMAN to "Kobieta")

@Serializable
data object SignUpScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(viewModel: SignUpScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val datePickerState = rememberDatePickerState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

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
            value = state.birthDate?.let {
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(it.toJavaInstant().atZone(ZoneId.systemDefault()))
            } ?: "",
            onValueChange = {},
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
            onExpandedChange = viewModel::changeShowSexDropdown
        ) {
            OutlinedTextField(
                value = state.sex?.let { SEX_LABELS[it] } ?: "",
                onValueChange = {},
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
            onClick = viewModel::handleSignUp
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