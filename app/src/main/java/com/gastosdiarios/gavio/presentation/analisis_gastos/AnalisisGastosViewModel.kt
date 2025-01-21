package com.gastosdiarios.gavio.presentation.analisis_gastos

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils.colorToHSL
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.bar_graph_custom.CircularBuffer
import com.gastosdiarios.gavio.data.constants.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import com.gastosdiarios.gavio.ui.theme.md_theme_dark_primary
import com.gastosdiarios.gavio.ui.theme.md_theme_dark_surfaceContainer
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AnalisisGastosViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val barDataFirestore: BarDataFirestore,
    private val userPreferencesFirestore: UserPreferencesFirestore,
    private val userDataFirestore: UserDataFirestore
) : ViewModel() {

    private val tag = "analisisGastosViewModel"
    private val circularBuffer = CircularBuffer(capacity = LIMIT_MONTH, db = barDataFirestore)

    private val _listBarDataModel = MutableStateFlow(ListUiState<BarDataModel>())
    val listBarDataModel = _listBarDataModel.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _uiState = MutableStateFlow<ListUiState<GastosPorCategoriaModel>>(ListUiState())
    var uiState = _uiState.stateIn(viewModelScope, SharingStarted.Lazily, ListUiState())

    private val _porcentajeGasto = MutableStateFlow<Int?>(0)
    val porcentajeGasto = _porcentajeGasto.asStateFlow()

    private var _totalIngresosRegistros = MutableStateFlow<Double?>(null)
    val totalIngresosRegister = _totalIngresosRegistros.asStateFlow()

    private var _icon = MutableStateFlow<String?>(null)
    val myIcon: StateFlow<String?> = _icon.asStateFlow()

    private val _isDarkMode = MutableStateFlow(ThemeMode.MODE_AUTO)
    val isDarkMode:StateFlow<ThemeMode> = _isDarkMode.asStateFlow()


    init {
        getAllListGastos()
        getDatosGastos()
        getDarkTheme()
    }

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
                val data = userDataFirestore.get()?.totalIngresos ?: 0.0
                //variable que muestra el progress de cada lista de itemCategory
                _totalIngresosRegistros.value = data
                calcularValorMaximo(data)
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el total de ingresos: ${e.message}")
            }
        }
    }

    private fun getDarkTheme() {
        viewModelScope.launch {
            val data = userPreferencesFirestore.get()?.themeMode
            _isDarkMode.update { data ?: ThemeMode.MODE_AUTO }
        }
    }

    // Función para calcular el valor máximo de la lista de transacciones
    private fun calcularValorMaximo(totalIngresos: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = dbm.getGastosPorCategoria()
                val maximoGastos = data.maxByOrNull { it.totalGastado ?: 0.0 }?.totalGastado ?: 0.0
                val icono = data.maxByOrNull { it.totalGastado ?: 0.0 }?.icon

                val porcentajeMes: Float =
                    MathUtils.calcularProgresoRelativo(totalIngresos, maximoGastos)
                val porcentaje: String = MathUtils.formattedPorcentaje(porcentajeMes)

                _porcentajeGasto.value = porcentaje.toInt()
                _icon.value = icono

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
                _listBarDataModel.update {
                    _listBarDataModel.value.copy(
                        items = data.reversed(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error en updateBarGraphList: ${e.message}")
            }
        }
    }
//------------------------------- FIN CIRCULAR BUFFER ---------------------------------//

    fun getRandomColor(isSystemInDarkTheme: Boolean):Pair<Color, Color> {
        var color = Pair(Color.Unspecified, Color.Unspecified)
        viewModelScope.launch {
            _isDarkMode.collect { mode ->
                color = when (mode) {
                    ThemeMode.MODE_AUTO -> {
                        if (isSystemInDarkTheme) {
                            getRandomDarkColor()
                        } else {
                            getRandomLightColor()
                        }
                    }

                    ThemeMode.MODE_DAY -> getRandomLightColor()
                    ThemeMode.MODE_NIGHT -> getRandomDarkColor()
                }
            }
        }
        return color
    }

    private fun getRandomLightColor(): Pair<Color, Color> {
        val hue = Random.nextInt(360).toFloat()
        // tertiaryContainer color
        val tertiaryContainerHSL = Color(0xFFF0F4F9).toHsl()
        val tertiaryContainerSaturation = tertiaryContainerHSL[1]
        val tertiaryContainerLightness = tertiaryContainerHSL[2]
        val tertiaryContainerColor = Color.hsl(hue, tertiaryContainerSaturation, tertiaryContainerLightness)

        // onTertiaryContainer color with same intensity
        val onTertiaryContainerHSL = Color(0xFF0B57D0).toHsl()
        val onTertiaryContainerSaturation = onTertiaryContainerHSL[1]
        val onTertiaryContainerLightness = onTertiaryContainerHSL[2]
        val onTertiaryContainerColor = Color.hsl(hue, onTertiaryContainerSaturation, onTertiaryContainerLightness)
     return Pair(tertiaryContainerColor, onTertiaryContainerColor)
    }

    private fun getRandomDarkColor(): Pair<Color, Color>  {
        val container = md_theme_dark_surfaceContainer
        val onContainer = md_theme_dark_primary
        return Pair(container, onContainer)
    }

    private fun Color.toHsl(): FloatArray {
        val hsl = FloatArray(3)
        colorToHSL(this.toArgb(), hsl)
        return hsl
    }

    fun refreshData(context: Context) {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                getAllListGastos()
                getDatosGastos()
                getDarkTheme()
                Toast.makeText(context, "actualizado", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

