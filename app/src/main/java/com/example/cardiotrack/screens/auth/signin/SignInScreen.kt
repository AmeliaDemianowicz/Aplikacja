package com.example.cardiotrack.screens.auth.signin

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.cardiotrack.services.auth.FirebaseAuthService
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Obiekt reprezentujący ekran logowania w systemie routingu.
 * Używany przy nawigacji jako identyfikator `SignInScreen`.
 */
@Serializable
data object SignInScreen
/**
 * Główny ekran logowania użytkownika.
 *
 * Ekran zawiera pola do wprowadzenia adresu e-mail oraz hasła,
 * a także przyciski do logowania i przejścia do rejestracji.
 *
 * @param viewModel ViewModel odpowiedzialny za obsługę logiki logowania.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(viewModel: SignInScreenViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()


    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var birthDate by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val formatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                .format(Calendar.getInstance().apply {
                    set(year, month, day)
                }.time)
            birthDate = formatted
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                .padding(bottom = 32.dp),
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


        Spacer(modifier = Modifier.padding(20.dp))

        FilledTonalButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignIn
        ) {
            Text("Zaloguj się")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading,
            onClick = viewModel::handleSignUp

        ) {
            Text("Zarejestruj się")
        }
    }

}
/**
 * Podgląd ekranu logowania w trybie podglądu Compose.
 *
 * Używany do wizualizacji UI w edytorze bez uruchamiania aplikacji.
 */
@Preview
@Composable
fun SignInScreenPreview() {
    val navController = rememberNavController()
    SignInScreen(
        viewModel = viewModel(factory = viewModelFactory {
            initializer { SignInScreenViewModel(FirebaseAuthService(), navController) }
        })
    )
}