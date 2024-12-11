package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.EventHandler
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.DarkMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AjustesViewModel @Inject constructor(private val dataStorePreferences: DataStorePreferences) :
    ViewModel() {

    private val _state = MutableStateFlow(DarkMode(ModeDarkThemeEnum.MODE_AUTO))
    val state: StateFlow<DarkMode> = _state.asStateFlow()

    private val _selectedMode = mutableStateOf(ModeDarkThemeEnum.MODE_AUTO)
    val selectedMode: State<ModeDarkThemeEnum> = _selectedMode

    init { getDarkTheme() }

    fun onEventHandler(e: EventHandler) {
        when (e) {
            is EventHandler.SelectedDarkThemeMode -> {
                _selectedMode.value = e.mode
                    setDarkMode()
                    applyDarkMode(e.mode)
            }
        }
    }

    private fun getDarkTheme() {
        viewModelScope.launch {
            dataStorePreferences.darkModeFlows.collect { darkMode ->
                _selectedMode.value = darkMode.mode // Acceder al modo con darkMode.mode
            }
        }
    }

    private fun setDarkMode() {
        if (selectedMode.value == ModeDarkThemeEnum.MODE_AUTO) {
            _state.update { it.copy(mode = ModeDarkThemeEnum.MODE_AUTO) }
        }
    }

    private fun applyDarkMode(mode: ModeDarkThemeEnum) {
        viewModelScope.launch {
            dataStorePreferences.updateDarkMode(mode) // Guardar el modo en DataStore
        }
    }
}
