package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val dbm: DataBaseManager,
) : ViewModel() {

    private val _themeModeChanged = MutableSharedFlow<ThemeMode>()
    val themeModeChanged: SharedFlow<ThemeMode> = _themeModeChanged.asSharedFlow()

    private val _uiState = MutableStateFlow<UiStateSingle<UserPreferences?>>(UiStateSingle.Loading)
    val uiState = _uiState.onStart { getUserPreferences() }
        .catch { throwable ->
            _uiState.update { UiStateSingle.Error(throwable = throwable) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateSingle.Loading)


    private fun getUserPreferences() {
        viewModelScope.launch {
            try {
                val data: UserPreferences? = dbm.getUserPreferences()
                _uiState.update { UiStateSingle.Success(data) }
            } catch (e: Exception) {
                _uiState.update { UiStateSingle.Error(throwable = e) }
            }
        }
    }


    fun updateBiometricSecurity(newState: Boolean) {
        viewModelScope.launch {
            try {
                val currentData = when (val currentState = _uiState.value) {
                    is UiStateSingle.Success -> currentState.data
                    else -> null
                }
                if (currentData != null) {
                    val updatedData = currentData.copy(biometricSecurity = newState)
                    try {
                        dbm.updateBiometricSecurity(newState)
                        _uiState.update { UiStateSingle.Success(updatedData) }
                    } catch (e: Exception) {
                        _uiState.update { UiStateSingle.Error(throwable = e) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { UiStateSingle.Error(throwable = e) }
            }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            try {
                val currentData = when (val currentState = _uiState.value) {
                    is UiStateSingle.Success -> currentState.data
                    else -> null
                }

                if (currentData != null) {
                    val updatedData = currentData.copy(themeMode = themeMode)
                    try {
                        dbm.updateThemeMode(themeMode)
                        _uiState.update { UiStateSingle.Success(updatedData) }

                        _themeModeChanged.emit(themeMode)
                    } catch (e: Exception) {
                        _uiState.update { UiStateSingle.Error(throwable = e) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { UiStateSingle.Error(throwable = e) }
            }
        }
    }
}