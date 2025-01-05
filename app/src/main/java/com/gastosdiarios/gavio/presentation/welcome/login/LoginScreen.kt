package com.gastosdiarios.gavio.presentation.welcome.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.welcome.login.components.ButtonLogin
import com.gastosdiarios.gavio.presentation.welcome.login.components.EmailField
import com.gastosdiarios.gavio.presentation.welcome.login.components.PasswordField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    email: String,
    password: String,
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val isError by remember { mutableStateOf(false) }
    val isShowSnackbar = remember { SnackbarHostState() }
    val passwordFocusRequester = remember { FocusRequester() }

    // Navegar en caso de éxito
    loginSuccess?.let { isSuccess ->
        if (isSuccess) {
            LaunchedEffect(loginSuccess) {
                navigateToHome()
                viewModel.resetLoginSuccess()
            }
        }
    }

    // Mostrar snackbar
    snackbarMessage?.let { messageResId ->
        val context = LocalContext.current
        LaunchedEffect(snackbarMessage) {
            isShowSnackbar.showSnackbar(context.getString(messageResId))
            viewModel.resetSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login", color = MaterialTheme.colorScheme.onBackground) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues: PaddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                .imePadding()
        ) {
            Text(
                text = stringResource(id = R.string.email),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.titleMedium
            )

            EmailField(
                value = email.ifEmpty { uiState.email },
                onNewValue = viewModel::onEmailChange,
                emailError = isError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                onNext = { passwordFocusRequester.requestFocus() }
            )
            Text(
                text = stringResource(id = R.string.password),
                modifier = Modifier.padding(top = 10.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            PasswordField(
                valuePassword = password.ifEmpty { uiState.password },
                onNewValue = viewModel::onPasswordChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.weight(1f))
            ButtonLogin(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                title = stringResource(id = R.string.iniciar_sesion),
                onClick = { viewModel.onSignUpClick() }) {
                viewModel.enabledButtonRegister()
            }
        }
    }
}