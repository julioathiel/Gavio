package com.gastosdiarios.gavio.presentation.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.SplashUiState
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val authFirebaseImp: AuthFirebaseImp
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        comprobando()
    }

    private fun comprobando() {
        viewModelScope.launch {
            val currentUser = authFirebaseImp.getCurrentUser()
            if (currentUser != null) {
                _uiState.update { it.copy(userRegistered = true) }
                getBiometricSelect()
            }else{
                _uiState.update { it.copy(userRegistered = false,startDestination = null, isLoading = false) }
            }
        }

    }

    private fun getBiometricSelect() {
        viewModelScope.launch {
            dataStorePreferences.getBiometric().collect { state ->
                _uiState.update { it.copy(securityActivated = state.securityActivated) }
                delay(1.seconds)
                _uiState.update{it.copy(isLoading = false)}
            }
        }
    }
}