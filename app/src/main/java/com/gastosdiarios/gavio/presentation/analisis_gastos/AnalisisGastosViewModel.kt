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
import com.gastosdiarios.gavio.data.domain.enums.ThemeMode
import com.gastosdiarios.gavio.data.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.ui.theme.md_theme_dark_primary
import com.gastosdiarios.gavio.ui.theme.md_theme_dark_surfaceContainer
import com.gastosdiarios.gavio.utils.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AnalisisGastosViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dbm: DataBaseManager,
    private val barDataFirestore: BarDataFirestore,
) : ViewModel() {
    private val tag = "analisisGastosViewModel"

    private val _uiState =
        MutableStateFlow<UiStateList<GastosPorCategoriaModel>>(UiStateList.Loading)
    val uiState = _uiState.onStart { getAllListGastos() }
        .catch { throwable ->
            _uiState.value = UiStateList.Error(throwable = throwable)
        }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000L),
            UiStateList.Loading
        )

    private val circularBuffer = CircularBuffer(capacity = LIMIT_MONTH, db = barDataFirestore)

    private val _listBarDataModel = MutableStateFlow<UiStateList<BarDataModel>>(UiStateList.Loading)
    val listBarDataModel = _listBarDataModel.onStart { updateBarGraphList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _showTwoColumns = MutableStateFlow(false)
    val showTwoColumns: StateFlow<Boolean> = _showTwoColumns.asStateFlow()

    private val _porcentajeGasto = MutableStateFlow<Int?>(0)
    val porcentajeGasto = _porcentajeGasto.asStateFlow()

    private var _totalIngresosRegistros = MutableStateFlow<Double?>(null)
    val totalIngresosRegister = _totalIngresosRegistros.asStateFlow()

    private var _icon = MutableStateFlow<String?>(null)
    val myIcon: StateFlow<String?> = _icon.asStateFlow()

    private val _isDarkMode = MutableStateFlow(ThemeMode.MODE_AUTO)
    val isDarkMode: StateFlow<ThemeMode> = _isDarkMode.asStateFlow()


    private fun getAllListGastos() {
        viewModelScope.launch(Dispatchers.IO) {
            val gastosPorCategoriaFlow = dbm.getGastosPorCategoria()
            val totalIngresosFlow = dbm.getUserData().map { it.totalIngresos }
            val themeModeFlow = dbm.getUserPreferences().map { it.themeMode }

            val combinedData = combine(
                gastosPorCategoriaFlow,
                totalIngresosFlow,
                themeModeFlow
            ) { gastos, ingresos, theme ->
                Triple(gastos, ingresos, theme)
            }.first()

            val (gastos, ingresos, themeMode) = combinedData
            if (gastos.isNotEmpty()) {
                _uiState.update { UiStateList.Success(gastos) }
            } else {
                _uiState.update { UiStateList.Empty }
            }
            _totalIngresosRegistros.value = ingresos
            if (ingresos != null) {
                calcularValorMaximo(ingresos)
            }
            if (themeMode != null) {
                _isDarkMode.value = themeMode
            }
        }
    }

    // Función para calcular el valor máximo de la lista de transacciones
    private fun calcularValorMaximo(totalIngresos: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = dbm.getGastosPorCategoria().first()
                val maximoGastos = db.maxByOrNull { it.totalGastado ?: 0.0 }?.totalGastado ?: 0.0
                val icono = db.maxByOrNull { it.totalGastado ?: 0.0 }?.icon

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
        try {
            viewModelScope.launch(Dispatchers.Main) {
                val mesActual = DateUtils.currentMonthNumber()
                // Obtener la lista actual de datos de gráficos
                val db = dbm.getBarDataGraph().first()
                val existingItem = db.find { it.monthNumber == mesActual }
                val newBarData = BarDataModel(
                    value = porcentajeMes,
                    money = maximoGastos.toString(),
                    monthNumber = mesActual,
                    index = existingItem?.index
                )
                if (existingItem != null && (existingItem.value ?: 0f) < porcentajeMes) {
                    circularBuffer.updateBarGraphItem(newBarData, db)
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
                val data: List<BarDataModel> = circularBuffer.getBarDataList()
                Log.d(tag, "updateBarGraphList: $data")
                if (data.isEmpty()) {
                    _listBarDataModel.update { UiStateList.Empty }
                } else {
                    _listBarDataModel.update { UiStateList.Success(data) }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error en updateBarGraphList: ${e.message}")
            }
        }
    }

//------------------------------- FIN CIRCULAR BUFFER ---------------------------------//

    fun getRandomColor(isSystemInDarkTheme: Boolean): Pair<Color, Color> {
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
        val tertiaryContainerColor =
            Color.hsl(hue, tertiaryContainerSaturation, tertiaryContainerLightness)

        // onTertiaryContainer color with same intensity
        val onTertiaryContainerHSL = Color(0xFF0B57D0).toHsl()
        val onTertiaryContainerSaturation = onTertiaryContainerHSL[1]
        val onTertiaryContainerLightness = onTertiaryContainerHSL[2]
        val onTertiaryContainerColor =
            Color.hsl(hue, onTertiaryContainerSaturation, onTertiaryContainerLightness)
        return Pair(tertiaryContainerColor, onTertiaryContainerColor)
    }

    private fun getRandomDarkColor(): Pair<Color, Color> {
        val container = md_theme_dark_surfaceContainer
        val onContainer = md_theme_dark_primary
        return Pair(container, onContainer)
    }

    private fun Color.toHsl(): FloatArray {
        val hsl = FloatArray(3)
        colorToHSL(this.toArgb(), hsl)
        return hsl
    }

    fun refreshData() {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                getAllListGastos()
                Toast.makeText(context, "actualizado", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun setToggleTwoColumns(boolean: Boolean) {
        _showTwoColumns.value = boolean
    }
}