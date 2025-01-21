package com.gastosdiarios.gavio.presentation.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.SplashUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesFirestore: UserPreferencesFirestore,
    private val authFirebaseImp: AuthFirebaseImp
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        comprobando()
    }

    private fun comprobando() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Iniciar la carga
            val currentUser = authFirebaseImp.getCurrentUser()
            if (currentUser != null) {
                _uiState.update { it.copy(userRegistered = true, isLoading = false) }
                getBiometricSelect()
            } else {
                // El usuario no esta registrado
                _uiState.update {
                    it.copy(
                        userRegistered = false,
                        isLoading = false // Detener la carga
                    )
                }
            }
        }
    }

    private fun getBiometricSelect() {
        viewModelScope.launch {
            val data: UserPreferences? = withContext(Dispatchers.IO) { userPreferencesFirestore.get() }
            _uiState.update { it.copy(securityActivated = data?.biometricSecurity ?: false) }
        }
    }
}