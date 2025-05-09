package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.ui_state.FechaMaximaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActualizarMaximoFechaViewModel @Inject constructor(
    private val dbm: DataBaseManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FechaMaximaUiState())
    val uiState: StateFlow<FechaMaximaUiState> = _uiState.asStateFlow()

    init {
        getFechaMaxima()
    }

    private fun getFechaMaxima() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.getUserPreferences().collect{ db->
                _uiState.update { it.copy(selectedOption = db.limitMonth ?: 0) }
            }
        }
    }

    // Agrega la función para guardar la fecha máxima
    fun updateFechaMaxima(option: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.updateLimitMonth(option)
        }
    }

}