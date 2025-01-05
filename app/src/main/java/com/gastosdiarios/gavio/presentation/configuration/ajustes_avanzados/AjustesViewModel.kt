package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.AjustesUiState
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.SeguridadBiometrica
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(AjustesUiState())
    val uiState: StateFlow<AjustesUiState> = _uiState.asStateFlow()
    private val _isDarkMode = MutableStateFlow(ModeDarkThemeEnum.MODE_AUTO)
    val isDarkMode: StateFlow<ModeDarkThemeEnum> = _isDarkMode.asStateFlow()

    init {
        getDarkTheme()
        getBiometric()
    }


    private fun getDarkTheme() {
        viewModelScope.launch {
            dataStorePreferences.getThemeMode().collect { mode ->
                _isDarkMode.value = mode.mode
                _uiState.update { it.copy(selectedMode = mode.mode) }
            }
        }
    }


    fun setThemeMode(mode: ModeDarkThemeEnum) {
        viewModelScope.launch {
            dataStorePreferences.updateDarkMode(mode) // Guardar el modo en DataStore
        }
    }


    fun setBiometric(isChecked: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setBiometric(SeguridadBiometrica(isChecked))
        }
    }


    private fun getBiometric() {
        viewModelScope.launch {
            dataStorePreferences.getBiometric().collect {
                _uiState.update { it.copy(securityBiometric = it.securityBiometric) }
            }
        }
    }

}
