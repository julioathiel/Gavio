package com.gastosdiarios.gavio.presentation.welcome.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.welcome.login.components.EmailField
import com.gastosdiarios.gavio.presentation.welcome.login.components.PasswordField
import com.gastosdiarios.gavio.presentation.welcome.register.components.ButtonRegister

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onToLogin: (email: String, password: String) -> Unit
) {
    val uiState by viewModel.uiState
    val registerState = viewModel.registerState.value
    val passwordFocusRequester = remember { FocusRequester() }


    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.crear_cuenta),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )
    }, containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        val context = LocalContext.current
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                //  .navigationBarsPadding() //Este modificador agrega un relleno en la parte inferior del Composable, asegurando que tu contenido no se superponga con la barra de navegación del sistema (si está presente).
                .imePadding() //Este modificador agrega un relleno en la parte inferior del Composable, asegurando que tu contenido no se superponga con el método de entrada suave (IME), que es el teclado en pantalla.

        ) {
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = stringResource(id = R.string.email),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )

            EmailField(
                value =  uiState.email,
                onNewValue = viewModel::onEmailChange,
                emailError = false,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                onNext = { passwordFocusRequester.requestFocus() }
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = stringResource(id = R.string.password),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            PasswordField(
                valuePassword = uiState.password,
                onNewValue = viewModel::onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Text(
                text = stringResource(id = R.string.password_error),
                color = Color.LightGray, style = MaterialTheme.typography.bodyLarge
            )
           Spacer(modifier = Modifier.weight(1f))

            ButtonRegister(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                title = stringResource(id = R.string.crear_cuenta),
                onClick = {
                    viewModel.onCreandoCuenta(context, uiState.email, uiState.password)
                }
            ) { viewModel.enabledButtonRegister() }
            // Ejecutar onToLogin() cuando registerState sea true
            LaunchedEffect(key1 = registerState) {
                if (registerState) {
                    onToLogin(uiState.email, uiState.password)
                }
            }
        }
    }
}