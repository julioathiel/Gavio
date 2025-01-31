package com.gastosdiarios.gavio.presentation.configuration

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ConfigurationUiState
import com.gastosdiarios.gavio.domain.model.OpcionEliminarModel
import com.gastosdiarios.gavio.domain.model.ShareDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val dataBarDataFirestore: BarDataFirestore
) : ViewModel() {

    private val _configurationUiState = MutableStateFlow(ConfigurationUiState())
    val configurationUiState: StateFlow<ConfigurationUiState> = _configurationUiState.asStateFlow()

    init {
        getShareLink()
    }

    private fun getShareLink() {
        viewModelScope.launch(Dispatchers.IO) {
            val data: ShareDataModel = dbm.getSharedLink()
            if (data.shareUrl != null) {
                _configurationUiState.update { it.copy(sharedLink = data.shareUrl) }
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
            val month = DateUtils.currentMonth()
            val entity = BarDataModel(value = 0f, month = month, money = "0")
            dataBarDataFirestore.create(entity)
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
        OpcionEliminarModel("Gastos programados",
            ::deleteGastosProgramados
        )
    )
}