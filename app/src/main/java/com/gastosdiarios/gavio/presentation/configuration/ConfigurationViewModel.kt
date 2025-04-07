package com.gastosdiarios.gavio.presentation.configuration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.bar_graph_custom.CircularBuffer
import com.gastosdiarios.gavio.data.ui_state.ConfigurationUiState
import com.gastosdiarios.gavio.data.domain.model.OpcionEliminarModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.utils.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val barDataFirestore: BarDataFirestore,
) : ViewModel() {

    private val _configurationUiState = MutableStateFlow(ConfigurationUiState())
    val configurationUiState: StateFlow<ConfigurationUiState> = _configurationUiState.asStateFlow()

    init {
        getShareLink()
    }

    private fun getShareLink() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.getSharedLink().collect { db ->
            if (db.shareUrl != null) {
                _configurationUiState.update { it.copy(sharedLink = db.shareUrl) }
            }
        }
        }
    }

    // Método para reiniciar la aplicación
    private fun resetData() {
        viewModelScope.launch {
            try {
                dbm.updateLimitMonth(31)
                dbm.updateHourMinute(21, 0)
                dbm.resetAllApp()
                _configurationUiState.update { it.copy(resetPending = true, resetComplete = true) }
            } catch (e: Exception) {
                Log.e("ErrorReinicioApp", "Error al reiniciar la aplicación: ${e.message}")
            }
        }
    }

    private fun deleteGraphBar() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.deleteAllGraphBar()
            val month = DateUtils.currentMonthNumber()
            val entity = BarDataModel(
                value = 0f,
                money = "0",
                monthNumber = month,
                index = 0
            )
         barDataFirestore.create(entity)
        }
    }

    private fun deleteUserCreaCatIngresos() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.deleteAllUserCreaCatIngresos()
        }
    }

    private fun deleteUserCreaCatGastos() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.deleteAllUserCreaCatGastos()
        }
    }

    private fun deleteGastosProgramados() {
        viewModelScope.launch(Dispatchers.IO) {
            dbm.deleteAllGastosProgramados()
        }
    }

    fun clearDatabase() = resetData()


    fun setResetPending(boolean: Boolean) {
        _configurationUiState.update { it.copy(resetPending = boolean) }
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
        OpcionEliminarModel(
            "Gastos programados",
            ::deleteGastosProgramados
        )
    )
}