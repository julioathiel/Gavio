package com.gastosdiarios.gavio.presentation.configuration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.ui_state.ConfigurationUiState
import com.gastosdiarios.gavio.domain.model.OpcionEliminarModel
import com.gastosdiarios.gavio.domain.model.ShareDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dataStorePreferences: DataStorePreferences,
    private val dbm: DataBaseManager,
    private val dataBarDataFirestore: BarDataFirestore
) : ViewModel() {

    private val _configurationUiState = MutableStateFlow(ConfigurationUiState())
    val configurationUiState: StateFlow<ConfigurationUiState> = _configurationUiState.asStateFlow()

    init {
        getShareLink()
    }

    // Método para reiniciar la aplicación
    private fun deleteData() {
        viewModelScope.launch {
            try {
                // Restablecer la opción seleccionada en el DataStore
                dataStorePreferences.setSelectedOption("31", true)
                dataStorePreferences.setHoraMinuto(21, 0)

                // Eliminar la lista de transacciones
                dbm.deleteAllApp()

                // Notificar a los observadores que la aplicación se reinició
                // con el resetPending finalizara la animacion de reinicio
                // con el resetComplete mostrara la pantalla de reinicio exitoso
                _configurationUiState.update { it.copy(resetPending = true) }
                _configurationUiState.update { it.copy(resetComplete = true) }
            } catch (e: Exception) {
                Log.e("ErrorReinicioApp", "Error al reiniciar la aplicación: ${e.message}")
            }
        }
    }

    private fun deleteGraphBar() {
        viewModelScope.launch {
            dbm.deleteAllGraphBar()
            val c = Calendar.getInstance()
            val month = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
            val entity = BarDataModel(value = 0f, month = month, money = "0")
            dataBarDataFirestore.create(entity)
        }
    }

    private fun deleteUserCreaCatIngresos() {
        viewModelScope.launch {
            dbm.deleteAllUserCreaCatIngresos()
        }
    }

    private fun deleteUserCreaCatGastos() {
        viewModelScope.launch {
            dbm.deleteAllUserCreaCatGastos()
        }
    }

    fun setShowShare(value: Boolean) {
        _configurationUiState.update { it.copy(showShareApp = value) }
    }

    fun setResetComplete(item: Boolean) {
        _configurationUiState.update { it.copy(showBottomSheet = false, resetComplete = item) }
    }

    fun setShowBottomSheet(value: Boolean) {
        _configurationUiState.update { it.copy(showBottomSheet = value) }
    }

    fun clearDatabase() {
        deleteData()
    }

    private fun getShareLink() {
        viewModelScope.launch {
            val data: ShareDataModel? = withContext(Dispatchers.IO) { dbm.getSharedLink() }
            if (data?.shareUrl != null) {
                _configurationUiState.update { it.copy(sharedLink = data.shareUrl) }
            }
        }
    }

    fun setResetPending(boolean: Boolean) {
        _configurationUiState.update { it.copy(resetPending = boolean) }
    }

    // Opciones de eliminación adicionales que son opcionales para el usuario
    val opcionesEliminar = listOf(
        OpcionEliminarModel(
            "Datos estadísticos del gráfico",
            ::deleteGraphBar
        ),
        OpcionEliminarModel(
            "Categorías de ingresos creadas",
            ::deleteUserCreaCatIngresos
        ),
        OpcionEliminarModel(
            "Categorías de gastos creadas",
            ::deleteUserCreaCatGastos
        ),
    )
}


