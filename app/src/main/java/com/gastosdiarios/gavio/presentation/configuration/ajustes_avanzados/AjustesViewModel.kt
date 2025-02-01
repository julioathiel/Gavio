package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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

    private val _uiState = MutableStateFlow<UiStateSingle<UserPreferences?>>(UiStateSingle.Loading)
    val uiState = _uiState.onStart { getUserPreferences() }
        .catch { throwable ->
            _uiState.update { UiStateSingle.Error(throwable.message ?: "Error desconocido") }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateSingle.Loading)


    private fun getUserPreferences() {
        viewModelScope.launch {
            try {
                val data: UserPreferences? = dbm.getUserPreferences()
                _uiState.update { UiStateSingle.Success(data) }
            } catch (e: Exception) {
                _uiState.update {UiStateSingle.Error(e.message ?: "Error desconocido") }
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
                if(currentData != null){
                    val updatedData = currentData.copy(biometricSecurity = newState)
                    try {
                        dbm.updateBiometricSecurity(newState)
                        _uiState.update { UiStateSingle.Success(updatedData) }

                    }catch (e:Exception){
                        _uiState.update { UiStateSingle.Error("Error saving to Firebase: ${e.message ?: "Unknown error"}") }
                    }
                }
            }catch (e:Exception){
                _uiState.update { UiStateSingle.Error(e.message ?: "Error desconocido") }
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
                         dbm.updateThemeMode(themeMode) // Saving to Firebase
                        _uiState.update { UiStateSingle.Success(updatedData) }
                    } catch (e: Exception) {
                        _uiState.update { UiStateSingle.Error("Error saving to Firebase: ${e.message ?: "Unknown error"}") }
                    }
                } else {
                    _uiState.update { UiStateSingle.Error("Error al obtener los datos") }
                }
            } catch (e: Exception) {
                _uiState.update { UiStateSingle.Error(e.message ?: "Unknown error") }
            }
        }
    }

}
