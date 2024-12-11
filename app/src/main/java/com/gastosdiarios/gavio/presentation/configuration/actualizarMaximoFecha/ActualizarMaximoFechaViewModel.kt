package com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActualizarMaximoFechaViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences
) : ViewModel() {
    private val _selectedOption = MutableLiveData(0)
    val selectedOption: LiveData<Int?> = _selectedOption

    private val _selectedSwitchOption = mutableStateOf(false)
    var selectedSwitchOption: State<Boolean> = _selectedSwitchOption

    init { obtenerFechaMaxima() }

    // Función para obtener la fecha máxima del DataStore
    private fun obtenerFechaMaxima() {
        viewModelScope.launch {
            dataStorePreferences.getFechaMaximoMes().collect{ fechaMaxima ->
                _selectedSwitchOption.value = fechaMaxima.switchActivado
                _selectedOption.value = fechaMaxima.numeroGuardado.toInt()
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