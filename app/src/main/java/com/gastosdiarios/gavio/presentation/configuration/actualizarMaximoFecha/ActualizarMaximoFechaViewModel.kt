package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.FechaMaximaUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActualizarMaximoFechaViewModel @Inject constructor(
    private val userPreferencesFirestore: UserPreferencesFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(FechaMaximaUiState())
    val uiState: StateFlow<FechaMaximaUiState> = _uiState.asStateFlow()

    init {
        getFechaMaxima()
    }

    private fun getFechaMaxima() {
        viewModelScope.launch {
            val data: UserPreferences? = userPreferencesFirestore.get()
            Log.d("data", data.toString())
            _uiState.update { it.copy(selectedOption = data?.dateMax ?: 0) }
        }
    }

    // Agrega la función para guardar la fecha máxima
    fun updateFechaMaxima(option: Int) {
        viewModelScope.launch {
            userPreferencesFirestore.updateDateMax(option)
        }
    }

}