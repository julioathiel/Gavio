package com.gastosdiarios.gavio.presentation.analisis_gastos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.bar_graph_custom.CircularBuffer
import com.gastosdiarios.gavio.data.constants.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.MathUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalisisGastosViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val barDataFirestore: BarDataFirestore
) : ViewModel() {
    private val tag = "analisisGastosViewModel"
      private val circularBuffer = CircularBuffer(capacity = LIMIT_MONTH, db = barDataFirestore)

    private val _listBarDataModel = MutableStateFlow(ListUiState<BarDataModel>())
    val listBarDataModel = _listBarDataModel.asStateFlow()

    private val _uiState = MutableStateFlow<ListUiState<GastosPorCategoriaModel>>(ListUiState())
    var uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, ListUiState())

    private val _porcentajeGasto = MutableStateFlow<Int?>(0)
    val porcentajeGasto = _porcentajeGasto.asStateFlow()

    private var _totalIngresosRegistros = MutableStateFlow<Double?>(null)
    val totalIngresosRegister = _totalIngresosRegistros.asStateFlow()

//    init {
//        getAllListGastos()
//        getDatosGastos()
//    }

    fun getAllListGastos() {
        _uiState.update { _uiState.value.copy(isLoading = true) }
        getAllGastos()
        getDatosGastos()
        _uiState.update { _uiState.value.copy(isLoading = false) }
    }

    private fun getAllGastos() {
        viewModelScope.launch(Dispatchers.IO) {
//            _uiState.update { _uiState.value.copy(isLoading = true) }
            val data = dbm.getGastosPorCategoria()
            _uiState.update { _uiState.value.copy(items = data) }
        }
    }

    private fun getDatosGastos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = dbm.getTotalIngresos()?.totalIngresos ?: 0.0
                //variable que muestra el progress de cada lista de itemCategory
                _totalIngresosRegistros.value = data
                calcularValorMaximo(data)
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el total de ingresos: ${e.message}")
            }
        }
    }

    // Función para calcular el valor máximo de la lista de transacciones
    private fun calcularValorMaximo(totalIngresos: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = dbm.getGastosPorCategoria()
                val maximoGastos = data.maxByOrNull { it.totalGastado ?: 0.0 }?.totalGastado ?: 0.0

                val porcentajeMes: Float = MathUtils.calcularProgresoRelativo(totalIngresos, maximoGastos)
                val porcentaje: String = MathUtils.formattedPorcentaje(porcentajeMes)
                _porcentajeGasto.value = porcentaje.toInt()
                insertGraph(maximoGastos, porcentaje.toFloat())
            } catch (e: Exception) {
                Log.e(tag, "Error en calcularValorMaximo : ${e.message}")
                insertGraph(0.0, 0f)
            }
        }
    }

    //-------------------------------CIRCULAR BUFFER ---------------------------------//
    // Función para insertar datos en la base de datos a través de CircularBuffer
    private fun insertGraph(maximoGastos: Double, porcentajeMes: Float) {
        Log.d(tag, "")
        try {
            viewModelScope.launch(Dispatchers.Main) {
                val mesActual = DateUtils.currentMonth()
                // Obtener la lista actual de datos de gráficos
                val listaGuardada = barDataFirestore.get()
                val existingItem = listaGuardada.find { it.month == mesActual } // Usar find
                if (existingItem != null && existingItem.value!! < porcentajeMes) {
                    circularBuffer.updateBarGraphItem(
                        BarDataModel(
                            value = porcentajeMes,
                            month = mesActual,
                            money = maximoGastos.toString()
                        ),
                        listaGuardada
                    )
                }
                // Actualizar la lista de datos de gráficos después de la inserción
                updateBarGraphList()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error en insertGraph: ${e.message}")
        }
    }
    // Función para obtener y actualizar la lista de datos de gráficos desde CircularBuffer
    private fun updateBarGraphList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _listBarDataModel.update { _listBarDataModel.value.copy(isLoading = true) }
                val data: List<BarDataModel> = circularBuffer.getBarGraphList()
                _listBarDataModel.update { _listBarDataModel.value.copy(items = data.reversed(), isLoading = false) }
            } catch (e: Exception) {
                Log.e(tag, "Error en updateBarGraphList: ${e.message}")
            }
        }
    }

//------------------------------- FIN CIRCULAR BUFFER ---------------------------------//
}

