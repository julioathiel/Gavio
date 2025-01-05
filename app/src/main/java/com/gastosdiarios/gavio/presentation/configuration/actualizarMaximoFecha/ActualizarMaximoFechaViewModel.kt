package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.FechaMaximaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActualizarMaximoFechaViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(FechaMaximaUiState())
    val uiState: StateFlow<FechaMaximaUiState> = _uiState.asStateFlow()

    init {
        getFechaMaxima()
    }

    // Función para obtener la fecha máxima del DataStore
    private fun getFechaMaxima() {
        viewModelScope.launch {
            dataStorePreferences.getFechaMaximoMes().collect { state ->
                _uiState.update {
                    it.copy(
                        switchActivado = state.switchActivado,
                        selectedOption = state.numeroGuardado.toInt()
                    )
                }
                Log.d("selectedSwitchNumber", "viewmodel obtiene ${_uiState.value.selectedOption}")
                Log.d("selectedSwitchNumber", "viewmodel obtiene ${_uiState.value.switchActivado}")
            }
        }
    }

    // Agrega la función para guardar la fecha máxima
    fun setSelectedOption(option: Int, selectedSwitchOption: Boolean) {
        viewModelScope.launch {
            dataStorePreferences.setSelectedOption(option.toString(), selectedSwitchOption)
        }
    }

}