package com.gastosdiarios.gavio.presentation.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.SplashUiState
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dbm:DataBaseManager,
    private val authFirebaseImp: AuthFirebaseImp
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkUserAndSecurity()
    }

    private fun checkUserAndSecurity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Iniciar la carga
            val currentUser = authFirebaseImp.getCurrentUser()
            val biometricSecurity = if (currentUser != null) {
                dbm.getUserPreferences()?.biometricSecurity ?: false
            } else {
                // Usuario no registrado, seguridad no aplicable
                false
            }
            _uiState.update {
                it.copy(
                    userRegistered = currentUser != null,
                    securityActivated = biometricSecurity,
                    isLoading = false // Detener la carga
                )
            }
        }
    }
}