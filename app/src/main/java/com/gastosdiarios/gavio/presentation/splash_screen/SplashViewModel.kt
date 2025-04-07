package com.gastosdiarios.gavio.presentation.splash_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.SplashUiState
import com.gastosdiarios.gavio.data.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.data.repository.DataBaseManager
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
    private val dbm: DataBaseManager,
    private val authFirebaseImp: AuthFirebaseImp
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkUserAndSecurity()
    }

    private fun checkUserAndSecurity() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) } // Iniciar la carga
            val currentUser = authFirebaseImp.getCurrentUser()
            if (currentUser != null) {
                dbm.getUserPreferences().collect { db ->
                    _uiState.update {
                        it.copy(
                            userRegistered = true,
                            securityActivated = db.biometricSecurity ?: false,
                            isLoading = false // Detener la carga
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        userRegistered = false,
                        securityActivated = false,
                        isLoading = false // Detener la carga
                    )
                }
            }
        }
    }
}