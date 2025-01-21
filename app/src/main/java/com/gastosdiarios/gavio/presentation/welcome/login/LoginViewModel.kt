package com.gastosdiarios.gavio.presentation.welcome.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.SnackbarManager
import com.gastosdiarios.gavio.data.constants.Constants.MIN_PASS_LENGTH
import com.gastosdiarios.gavio.data.constants.Constants.PASS_PATTERN
import com.gastosdiarios.gavio.data.ui_state.LoginUiState
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authFirebaseImp: AuthFirebaseImp
) : ViewModel() {
    val tag = "loginViewModel"
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> get() = _loginSuccess

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    val snackbarMessage: StateFlow<Int?> get() = _snackbarMessage

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignUpClick() {
        if (email.isEmpty() || password.isEmpty()) {
            _snackbarMessage.value = R.string.password_error
            return
        }
        viewModelScope.launch {
            authFirebaseImp.signInWithEmailAndPassword(uiState.value.email, uiState.value.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _loginSuccess.value = true
                        uiState.value = uiState.value.copy(email = "", password = "")
                    } else {
                        _loginSuccess.value = false
                        val exception = task.exception
                        when (exception) {
                            is FirebaseAuthInvalidUserException -> {
                                // La cuenta no existe o está deshabilitada
                                _snackbarMessage.value = R.string.la_cuenta_no_existe_o_esta_deshabilitada
                            }

                            is FirebaseAuthInvalidCredentialsException -> {
                                // Contraseña incorrecta
                                _snackbarMessage.value = R.string.contraseña_incorrecta
                            }

                            else -> {
                                // Otro error de autenticación
                                _snackbarMessage.value = R.string.authentication_failed
                            }
                        }
                        _snackbarMessage.value = R.string.email_error
                    }
                }
        }

        if (!password.isValidPassword()) {
            SnackbarManager.showMessage(R.string.password_error)
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            SnackbarManager.showMessage(R.string.password_match_error)
            return
        }
    }

    // Método para verificar si el correo electrónico es válido
    fun enabledButtonRegister(): Boolean {
        //si la direccion de email es valida, que contenga un @, un punto, etc
        return password.isValidPassword() && email.isValidEmail()
    }

    private fun String.isValidEmail(): Boolean {
        return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun String.isValidPassword(): Boolean {
        return this.isNotBlank() &&
                this.length >= MIN_PASS_LENGTH &&
                Pattern.compile(PASS_PATTERN).matcher(this).matches()
    }

    private fun String.passwordMatches(repeated: String): Boolean {
        return this == repeated
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = null
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }
}
