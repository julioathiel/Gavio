package com.gastosdiarios.gavio.presentation.home

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.bar_graph_custom.CircularBuffer
import com.gastosdiarios.gavio.data.commons.SnackbarManager
import com.gastosdiarios.gavio.data.commons.SnackbarMessage
import com.gastosdiarios.gavio.data.constants.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoriesModel
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.DateUtils.agregandoUnMes
import com.gastosdiarios.gavio.utils.DateUtils.converterFechaPersonalizada
import com.gastosdiarios.gavio.utils.DateUtils.obtenerFechaActual
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val resources: Resources,
    @ApplicationContext private val context: Context,
    private val authFirebaseImp: AuthFirebaseImp,
    private val dbm: DataBaseManager,
    private val transactionsFirestore: TransactionsFirestore,
    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val gastosProgramadosFirestore: GastosProgramadosFirestore,
    barDataFirestore: BarDataFirestore,
    val snackbarManager: SnackbarManager
) : ViewModel() {

    private val tag = "homeViewModel"


    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _listFilter = mutableStateListOf<GastosProgramadosModel>()
    val listFilter: List<GastosProgramadosModel> = _listFilter


    private val circularBuffer = CircularBuffer(capacity = LIMIT_MONTH, db = barDataFirestore)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.onStart {
        calculandoInit()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        false
    )


    private fun calculandoInit() {
        viewModelScope.launch {
            _isLoading.update { true }
            try {
                val fechaActual = obtenerFechaActual()// muestra 2025-01-01
                val data = dbm.getUserData()
                val dataCurrentMoney = data?.currentMoney
                val dataDate = data?.selectedDate

                // Verificar si fechaGuardada es nula o esta vacia antes de intentar analizarla
                when (dataDate?.isNotEmpty()) {
                    true -> {

                        val fechaLocalDate = DateUtils.toLocalDate(dataDate)

                        // ej: 2023-12-12
                        //si la fecha actual es igual que la fecha guardada
                        if (fechaActual == fechaLocalDate) {
                            if (dataCurrentMoney == 0.0) {
                                updateFechaUnMesMas(fechaActual, fechaLocalDate)
                                _homeUiState.update { it.copy(showNuevoMes = true) }
                            } else if (dataCurrentMoney != 0.0) {
                                //si aun tiene dinero el usuario al finalizar la fecha elegida
                                updateFechaUnMesMas(fechaActual, fechaLocalDate)
                                //  updateTotalIngresos(TotalIngresosModel(totalIngresos = dataCurrentMoney))
                                updateIngresos(dataCurrentMoney ?: 0.0)
                                // Insertar un nuevo elemento
                                circularBuffer.adjustBufferCapacityIfNeeded()
                                circularBuffer.createBarGraph(
                                    BarDataModel(
                                        value = 0f,
                                        month = DateUtils.currentMonth()!!,
                                        money = "0"
                                    )
                                )
                                dbm.updateTotalGastos(0.0)
                                dbm.deleteAllTransactions()

                                crearTransaction(
                                    cantidad = dataCurrentMoney.toString(),
                                    categoryName = getString(R.string.saldo_restante),
                                    description = "",
                                    categoryIcon = R.drawable.ic_sueldo,
                                    tipoTransaccion = TipoTransaccion.INGRESOS
                                )
                                //actualizando con el nuevo valor maximo del progress
                                _homeUiState.update {
                                    _homeUiState.value.copy(
                                        mostrandoDineroTotalIngresos = dataCurrentMoney,
                                        showNuevoMes = true
                                    )
                                }
                                initMostrandoAlUsuario()
                            } else {
                                //si el usuario no tiene dinero al llegar la fecha entonces se reinician los datos del progress
                                reseteandoProgress()
                            }
                        } else {
                            //si aun no es la fecha elegida por el usuario se mostrara esto
                            initMostrandoAlUsuario()
                        }
                    }

                    false -> {
                        // Si fechaGuardada es nula y dias restantes tambien se asigna un valor predeterminado
                        manejarFechayDiasRestantesNulos()
                    }

                    null -> {
                        // Si fechaGuardada es nula y dias restantes tambien se asigna un valor predeterminado
                        initMostrandoAlUsuario()
                        //  manejarFechayDiasRestantesNulos()
                    }
                }
                _isLoading.update { false }
            } catch (e: Exception) {
                Log.e(tag, "calculandoInit: error: ${e.message}")
                manejarFechayDiasRestantesNulos()
            }
        }
    }

    private fun reseteandoProgress() {
        viewModelScope.launch {
            //si el usuario gasto el dinero antes del dia de la fecha elegida
            dbm.updateAllApp()
        }
    }

    private fun updateFechaUnMesMas(fechaActual: LocalDate, fechaParseada: LocalDate) {
        viewModelScope.launch {
            val nuevoMes: LocalDate = agregandoUnMes(fechaActual, fechaParseada)
            updateDate(nuevoMes.toString())
            // Eliminar la lista solo si no estamos en el último día del mes actual
            if (nuevoMes > fechaActual) {
                dbm.deleteAllGastosPorCategory()
            }
        }
    }

    private fun initMostrandoAlUsuario() {
        viewModelScope.launch {
            try {
                getMaxDate()
                dbm.getUserData()
                getGastosProgramados()
                mostrarCurrentMoney()
                mostrarTotalIngresos()
                mostrarTotalGastos()
                mostrarEstadoUsuario()
                listCatGastosNueva()
                listCatIngresosNueva()
                _homeUiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _homeUiState.update { it.copy(isError = true) }
                Log.e(tag, "initMostrandoAlUsuario no hay internet: ${e.message}")
            }
        }
    }

    private fun mostrandoAlUsuario(fecha: String) {
        mostrarFecha(fecha)
        mostrarDiasRestantes(fecha)
    }

    private fun mostrarEstadoUsuario() {
        viewModelScope.launch {
            try {
                val data = dbm.getUserData()
                val currentMoney = data?.currentMoney
                val selectedDate = data?.selectedDate

                if (selectedDate != null) {
                    mostrandoAlUsuario(selectedDate)
                    mostrarLimitePorDia(currentMoney, selectedDate)
                } else {
                    _homeUiState.update {
                        _homeUiState.value.copy(
                            fechaElegida = "",
                            diasRestantes = 0,
                            limitePorDia = 0.0
                        )
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Error en mostrarEstadoUsuario", Toast.LENGTH_SHORT)
                    .show()
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarCurrentMoney() {
        viewModelScope.launch {
            val data = dbm.getUserData()
            val currentMoney = data?.currentMoney ?: 0.0
            _homeUiState.update { it.copy(dineroActual = currentMoney) }
        }

    }

    private fun mostrarFecha(fecha: String) {
        val date: String = converterFechaPersonalizada(fecha)
        _homeUiState.update { it.copy(fechaElegida = date) }
    }

    private fun mostrarDiasRestantes(fecha: String) {
        val diasRestantes = getDiasRestantes(fecha)
        _homeUiState.update { it.copy(diasRestantes = diasRestantes) }
    }

    private fun mostrarTotalIngresos() {
        viewModelScope.launch {
            try {
                val data = dbm.getUserData()
                val totalIngresos = data?.totalIngresos ?: 0.0
                _homeUiState.update { it.copy(mostrandoDineroTotalIngresos = totalIngresos) }
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarTotalGastos() {
        viewModelScope.launch {
            try {
                val data = dbm.getUserData()

                val totalGastos = data?.totalGastos ?: 0.0
                _homeUiState.update {
                    it.copy(mostrandoDineroTotalGastos = totalGastos)
                }

            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarLimitePorDia(currentMoney: Double?, selectedDate: String?) {
        val diasRestantes = getDiasRestantes(selectedDate)

        val limitePorDia =
            MathUtils.getLimitePorDia(currentMoney ?: 0.0, diasRestantes = diasRestantes)
        _homeUiState.update { _homeUiState.value.copy(limitePorDia = limitePorDia) }
    }


    private fun getMaxDate() {
        viewModelScope.launch {
            //obteniendo fecha guardada maxima por el usuario
            val data: Int = dbm.getUserPreferences()?.limitMonth ?: 0
            _homeUiState.update { it.copy(limitMonth = data) }
        }
    }

    private fun getDiasRestantes(getFechaGuion: String?): Int {
        if (getFechaGuion.isNullOrEmpty()) {
            return 0
        }
        val fechaLocalDate = DateUtils.toLocalDate(getFechaGuion)
        val fechaActual = obtenerFechaActual()
        val dia: Long = ChronoUnit.DAYS.between(fechaActual, fechaLocalDate)

        return dia.toInt()
    }

    //...FUNCION QUE SE USA CUANDO SE CREA UNA TRANSACTION
    fun cantidadIngresada(cantidadIngresada: String, tipoTransaccion: TipoTransaccion) {
        _homeUiState.update { _homeUiState.value.copy(cantidadIngresada = cantidadIngresada) }
        calulatorDialog(cantidadIngresada.toDouble(), tipoTransaccion)
    }

    //...FUNCION QUE SE USA PARA CALCULAR TOTALiNGRESOS, TOTALgASTOS, CURRENTMONEY
    private fun calulatorDialog(
        cantidadIngresada: Double,
        tipoTransaccion: TipoTransaccion
    ) {
        viewModelScope.launch {
            val data = dbm.getUserData()
            val totalIngresos = data?.totalIngresos ?: 0.0
            val totalGastos = data?.totalGastos ?: 0.0
            val currentMoney = data?.currentMoney ?: 0.0
            val currentMoneyIsZero = data?.currentMoneyIsZero ?: false
            val date = data?.selectedDate ?: ""

            val nuevoTotal = when (tipoTransaccion) {
                TipoTransaccion.INGRESOS -> addDiner(cantidadIngresada, totalIngresos)
                else -> addDiner(cantidadIngresada, totalGastos)
            }

            val dinerActual = when (tipoTransaccion) {
                TipoTransaccion.INGRESOS -> addDiner(cantidadIngresada, currentMoney)
                else -> maxOf(
                    restarDinero(cantidadIngresada.toString(), currentMoney),
                    0.0
                )
            }

            //si el usuario eligio ingresos
            when (tipoTransaccion) {
                TipoTransaccion.INGRESOS ->
                    if (currentMoneyIsZero) {
                        //data.checked es true entonces significa que no hay nada aun guardado
                        insertPrimerTotalIngresos(nuevoTotal)
                    } else {
                        //Si el usuario eligio Ingresos pero en data.isChecked es false
                        // significa que ya hay datos guardados, por ende se actualizaran los ingresos
                        // Si ya hay datos en db se actualiza el totalIngresos
                        updateIngresos(nuevoTotal)
                    }
                //Si el usuario eligio Gastos
                TipoTransaccion.GASTOS -> {
                    //si el usuario eligio gastos
                    if (dinerActual == 0.0 && nuevoTotal > 0.0) {
                        //reseteando el progress
                        reseteandoProgress()
                    } else {
                        // Si el usuario eligió gastos, actualizar la base de datos con el nuevo dinero
                        dbm.updateTotalGastos(nuevoTotal)
                        mostrarTotalGastos()
                    }
                }
            }
            //actualizando el dinero actual siempre
            dbm.updateCurrentMoney(dinerActual, false)
            mostrarCurrentMoney()
            mostrarLimitePorDia(currentMoney = dinerActual, selectedDate = date)
        }
    }


    private fun addDiner(cantidadIngresada: Double, dataTotal: Double): Double {
        val result = BigDecimal(cantidadIngresada + dataTotal)
            .setScale(2, RoundingMode.HALF_EVEN)
        return result.toDouble()
    }


    private fun insertPrimerTotalIngresos(totalIngresos: Double) {
        updateIngresos(totalIngresos)
        mostrarTotalIngresos()
    }

    fun insertUpdateFecha(fecha: String) {
        viewModelScope.launch {
            try {
                val data = dbm.getUserData()
                val selectedDate = data?.selectedDate

                if (selectedDate == null) {
                    //si es null se ingresa por primera vez
                    insertDate(fecha)
                    mostrarLimitePorDia(
                        currentMoney = data?.currentMoney,
                        selectedDate = ""
                    )

                } else {
                    //si es false se actualiza en la base de datos y por las dudas se sigue manteniendo que es false
                    updateDate(fecha)
                    mostrarLimitePorDia(currentMoney = data.currentMoney, selectedDate = fecha)
                }

            } catch (e: DateTimeParseException) {
                Toast.makeText(context, "Error en insertUpdateFecha", Toast.LENGTH_SHORT).show()
                Log.e(tag, "Error al analizar la fecha: $fecha", e)
            }
        }
    }


    private fun insertDate(fecha: String) {
        viewModelScope.launch {
            dbm.updateSelectedDate(fecha, false)
            _homeUiState.update { _homeUiState.value.copy(fechaElegida = fecha) }
            mostrandoAlUsuario(fecha)
            mostrarDiasRestantes(fecha)
        }

    }

    private fun updateDate(fecha: String) {
        viewModelScope.launch {
            dbm.updateSelectedDate(fecha, false)
            mostrandoAlUsuario(fecha)
        }
    }

    private fun updateIngresos(value: Double) {
        viewModelScope.launch {
            try {
                dbm.updateTotalIngresos(value)
                mostrarCurrentMoney()
                mostrarTotalIngresos()
            } catch (e: Exception) {
                Toast.makeText(context, "Error en updateTotalIngresoss", Toast.LENGTH_SHORT).show()
                Log.e(tag, "updateTotalIngresos: ${e.message}")
            }
        }
    }

    private fun restarDinero(nuevoValor: String, money: Double?): Double {
        val dineroActual = money ?: 0.0
        val result =
            BigDecimal(dineroActual - nuevoValor.toDouble()).setScale(
                2,
                RoundingMode.HALF_EVEN
            )
        return result.toDouble()
    }


    private fun listCatGastosNueva() {
        viewModelScope.launch {
            val newCategories = withContext(Dispatchers.IO) {
                dbm.getUserCategoryGastos().map { document ->
                    val date = UserCreateCategoriesModel(
                        nameCategory = document.categoryName ?: "",
                        categoryIcon = document.categoryIcon ?: ""
                    )
                    val icon = date.categoryIcon?.toIntOrNull() ?: 0
                    CategoryGastos(name = date.nameCategory!!, icon = icon)
                }
            }.distinctBy { it.name } // Elimina duplicados
            // Añade solo categorías nuevas que aún no existan
            newCategories.forEach { newCategory ->
                if (!defaultCategoriesGastosList.any { it.name == newCategory.name }) {
                    defaultCategoriesGastosList.add(newCategory)
                }
            }
        }
    }

    private fun listCatIngresosNueva() {
        viewModelScope.launch {
            val newCategories = withContext(Dispatchers.IO) {
                dbm.getUserCategoryIngresos().map { document ->
                    val date = UserCreateCategoriesModel(
                        nameCategory = document.categoryName ?: "",
                        categoryIcon = document.categoryIcon ?: ""
                    )
                    val icon = date.categoryIcon?.toIntOrNull() ?: 0
                    CategoryIngresos(name = date.nameCategory!!, icon = icon)
                }
            }.distinctBy { it.name }
            // Añade solo categorías nuevas que aún no existan
            newCategories.forEach { newCategory ->
                if (!defaultCategoriesIngresosList.any { it.name == newCategory.name }) {
                    defaultCategoriesIngresosList.add(newCategory)
                }
            }
        }
    }

    private fun manejarFechayDiasRestantesNulos() {
        // Si fechaGuardada es nula, maneja ese caso aquí
        _homeUiState.update { _homeUiState.value.copy(fechaElegida = "", diasRestantes = 0) }
    }

    fun crearNuevaCategoriaDeGastos(nameCategory: String, icon: Int, cantidadIngresada: String) {
        // Esta función se llamará desde tu Composable para crear
        // una nueva categoría de gastos unicas en el registro
        viewModelScope.launch(Dispatchers.IO) {
            val getGastos = dbm.getGastosPorCategoria()

            // Verificar si la categoría ya existe en la lista
            if (getGastos.any { it.title == nameCategory }) {
                val uid = getGastos.first { it.title == nameCategory }.uid
                val getCantidad = getGastos.first { it.title == nameCategory }.totalGastado
                val newTotalGastado = getCantidad!! + cantidadIngresada.toDouble()
                // Actualizar la cantidad en la lista
                val entity = GastosPorCategoriaModel(
                    uid = uid,
                    title = nameCategory,
                    icon = icon.toString(),
                    totalGastado = newTotalGastado
                )
                gastosPorCategoriaFirestore.update(entity)
            } else {
                gastosPorCategoriaFirestore.create(
                    GastosPorCategoriaModel(
                        title = nameCategory,
                        icon = icon.toString(),
                        totalGastado = cantidadIngresada.toDouble()
                    )
                )
            }
        }
    }

    fun crearTransaction(
        cantidad: String,
        categoryName: String,
        description: String,
        categoryIcon: Int,
        tipoTransaccion: TipoTransaccion,
    ) {

        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { dbm.getTransactions() }
            val newIndex = list.maxOfOrNull { it.index ?: 0 }?.plus(1) ?: 0

            transactionsFirestore.create(
                TransactionModel(
                    title = categoryName,
                    subTitle = description,
                    cash = cantidad,
                    tipoTransaccion = tipoTransaccion,
                    date = obtenerFechaActual().toString(),
                    icon = categoryIcon.toString(),
                    index = newIndex
                )
            )
        }
        onDialogClose()
        _homeUiState.update { _homeUiState.value.copy(cantidadIngresada = "", description = "") }
    }

    fun refreshData() {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                initMostrandoAlUsuario()
                Toast.makeText(context, "actualizado", Toast.LENGTH_SHORT).show()
            }
        )
    }


    //metodo que devuelve un true para que abra el dialogo de transaccion
    fun onShowDialogClickTransaction() {
        viewModelScope.launch {
            _homeUiState.update { _homeUiState.value.copy(showTransaction = true) }
            if (_homeUiState.value.dineroActual == 0.0 || _homeUiState.value.dineroActual == null) {
                _homeUiState.update {
                    _homeUiState.value.copy(
                        buttonIngresosActivated = 1,// el boton ingreso esta activado
                        tipoTransaccion = TipoTransaccion.INGRESOS,// el menu de ingresos esta activado
                        enabledButtonGastos = false //el boton de gastos esta desactivado
                    )
                }
            } else {
                _homeUiState.update {
                    _homeUiState.value.copy(
                        buttonIngresosActivated = 0,// el boton gastos esta activado
                        tipoTransaccion = TipoTransaccion.GASTOS,// el menu de gastos esta activado = false,// el menu de gastos esta activado
                        enabledButtonGastos = true //el boton de gastos esta activado
                    )

                }
            }
        }
    }

    fun onDialogClose() {
        _homeUiState.update {
            _homeUiState.value.copy(
                showTransaction = false,
                cantidadIngresada = "",
                description = ""
            )
        }
    }

    fun setIsChecked(value: TipoTransaccion) {
        _homeUiState.update { it.copy(tipoTransaccion = value) }
    }

    fun setShowNuevoMes(value: Boolean) {
        _homeUiState.update { it.copy(showNuevoMes = value) }
    }

    private fun getString(idRecurso: Int): String {
        return resources.getString(idRecurso)
    }

    fun getCurrentUser(): FirebaseUser? = authFirebaseImp.getCurrentUser()

    //--------------Gastos programados
    fun pagarItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            try {
                val data = dbm.getUserData()
                val totalIngresos: Double = data?.totalIngresos ?: 0.0
                val cash: Double = item.cash?.toDouble() ?: 0.0

                if (totalIngresos == 0.0) {
                    snackbarManager.showMessage(context.getString(R.string.no_hay_dinero_para_un_gasto))
                } else if (totalIngresos < cash) {
                    snackbarManager.showMessage(context.getString(R.string.agrega_mas_dinero_antes_de_pagar))
                } else {
                    clearItem(item)
                    crearTransaction(
                        cantidad = item.cash ?: "",
                        categoryName = item.title ?: "",
                        description = item.subTitle ?: "",
                        categoryIcon = item.icon?.toInt() ?: 0,
                        tipoTransaccion = TipoTransaccion.GASTOS
                    )
                        snackbarManager.showMessage(context.getString(R.string.pagado_con_exito))
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error en pagarItem", Toast.LENGTH_SHORT).show()
                Log.e(tag, "Error en pagarItem", e)
            }
        }
    }

    fun clearItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            try {
                _listFilter.remove(item)
                val nuevoMes: String =
                    DateUtils.toLocalDate(item.date ?: "").plusMonths(1).toString()
                gastosProgramadosFirestore.update(item.copy(date = nuevoMes))
            } catch (e: Exception) {
                Toast.makeText(context, "Error en clearItem", Toast.LENGTH_SHORT).show()
                Log.e(tag, "Error en clearItem", e)
            }
        }
    }

    private fun getGastosProgramados() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) { gastosProgramadosFirestore.get() }
                val filteredData = data.filter {
                    try {
                        val date: LocalDate = DateUtils.toLocalDate(it.date ?: "")
                        date.isBefore(obtenerFechaActual().plusDays(1)) || date.isEqual(
                            obtenerFechaActual()
                        )
                    } catch (e: DateTimeParseException) {
                        false
                    }
                }
                _listFilter.clear()
                _listFilter.addAll(filteredData)
            } catch (e: Exception) {
                Toast.makeText(context, "Error en getGastosprogramados", Toast.LENGTH_SHORT).show()
                Log.e(tag, "getGastosprogramados: error", e)
            }
        }
    }

    //--------------FIN-------Gastos programados -------------------------//

}