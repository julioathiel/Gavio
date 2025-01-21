package com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
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
class AjustesViewModel @Inject constructor(
    private val up: UserPreferencesFirestore
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(UserPreferences())
    val uiState: StateFlow<UserPreferences> = _uiState.asStateFlow()

    init {
        getUserPreferences()
    }


    private fun getUserPreferences() {
        viewModelScope.launch {
            val data: UserPreferences? = withContext(Dispatchers.IO) { up.get() }
            _uiState.update {
                it.copy(
                    biometricSecurity = data?.biometricSecurity ?: false,
                    themeMode = data?.themeMode ?: ThemeMode.MODE_AUTO,
                )
            }
        }
    }


    fun updateBiometricSecurity(newState: Boolean) {
        viewModelScope.launch {
            up.updateBiometricSecurity(newState)
            _uiState.update { it.copy(biometricSecurity = newState) }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            up.updateThemeMode(themeMode)
            _uiState.update { it.copy(themeMode = themeMode) }
        }
    }
}
