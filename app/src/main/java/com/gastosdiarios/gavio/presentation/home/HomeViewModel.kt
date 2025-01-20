package com.gastosdiarios.gavio.presentation.home

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.bar_graph_custom.CircularBuffer
import com.gastosdiarios.gavio.data.DataStorePreferences
import com.gastosdiarios.gavio.data.constants.Constants.LIMIT_MONTH
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoriesModel
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.DateModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalGastosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalIngresosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CurrentMoneyFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.DateFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosProgramadosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalIngresosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserPreferencesFirestore
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.DateUtils.agregandoUnMes
import com.gastosdiarios.gavio.utils.DateUtils.converterFechaABarra
import com.gastosdiarios.gavio.utils.DateUtils.converterFechaPersonalizada
import com.gastosdiarios.gavio.utils.DateUtils.obtenerFechaActual
import com.gastosdiarios.gavio.utils.DateUtils.parsearFechaALocalDate
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val resources: Resources,
    private val authFirebaseImp: AuthFirebaseImp,
    private val dbm: DataBaseManager,
    private val dataStorePreferences: DataStorePreferences,
    private val currentMoneyFirestore: CurrentMoneyFirestore,
    private val totalIngresosFirestore: TotalIngresosFirestore,
    private val totalGastosFirestore: TotalGastosFirestore,
    private val dateFirestore: DateFirestore,
    private val transactionsFirestore: TransactionsFirestore,
    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val gastosProgramadosFirestore: GastosProgramadosFirestore,
    private val up: UserPreferencesFirestore,
    private val udb: UserDataFirestore,
    barDataFirestore: BarDataFirestore
) : ViewModel() {

    private val tag = "homeViewModel"

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _listFilter = mutableStateListOf<GastosProgramadosModel>()
    val listFilter: List<GastosProgramadosModel> = _listFilter

    private val _transactionUiState = MutableStateFlow(ListUiState<TransactionModel>())

    private val circularBuffer = CircularBuffer(capacity = LIMIT_MONTH, db = barDataFirestore)

    init {
        calculandoInit()
    }


    private fun calculandoInit() {
        viewModelScope.launch {
            try {
                val fechaActual = obtenerFechaActual()// muestra 2025-01-01
                val dataCurrentMoney = dbm.getCurrentMoney()?.money
                val dataDate = dbm.getDate()?.date

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
                                updateTotalIngresos(TotalIngresosModel(totalIngresos = dataCurrentMoney))
                                // Insertar un nuevo elemento
                                circularBuffer.adjustBufferCapacityIfNeeded()
                                circularBuffer.createBarGraph(
                                    BarDataModel(
                                        value = 0f,
                                        month = DateUtils.currentMonth()!!,
                                        money = "0"
                                    )
                                )
                                totalGastosFirestore.createOrUpdate(TotalGastosModel(totalGastos = 0.0))
                                transactionsFirestore.deleteAll()
                                _transactionUiState.update { it.copy(items = emptyList()) }
                                crearTransaction(
                                    cantidad = dataCurrentMoney.toString(),
                                    categoryName = getString(R.string.saldo_restante),
                                    description = "",
                                    categoryIcon = R.drawable.ic_sueldo,
                                    isChecked = true
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
            } catch (e: Exception) {
                Log.e(tag, "calculandoInit: error: ${e.message}")
                manejarFechayDiasRestantesNulos()
            }
        }
    }

    private fun reseteandoProgress() {
        //si el usuario gasto el dinero antes del dia de la fecha elegida
        viewModelScope.launch {
            transactionsFirestore.deleteAll()
            gastosPorCategoriaFirestore.deleteAll()
            createOrUpdateCurrentMoney(CurrentMoneyModel(money = 0.0, checked = false))
            updateTotalIngresos(TotalIngresosModel(totalIngresos = 0.0))
            totalGastosFirestore.createOrUpdate(TotalGastosModel(totalGastos = 0.0))
        }
    }

    private fun updateFechaUnMesMas(fechaActual: LocalDate, fechaParseada: LocalDate) {
        viewModelScope.launch {
            //se agrega un mes a la fecha guardada
            val nuevoMes: String = agregandoUnMes(fechaActual, fechaParseada)// muestra 2025-02-01
            val nuevoMesLocalDate = parsearFechaALocalDate(nuevoMes)
            val fechaConBarra = converterFechaABarra(nuevoMes) // muestra 01/02/2025
            //se actualiza en la base de datos la nueva fecha
            updateDate(DateModel(date = fechaConBarra, isSelected = false))
            // Eliminar la lista solo si no estamos en el último día del mes actual
            if (nuevoMesLocalDate!! > fechaActual) {
                gastosPorCategoriaFirestore.deleteAll()
            }
        }
    }

    private fun initMostrandoAlUsuario() {
        viewModelScope.launch {
            try {

                _homeUiState.update { it.copy(isLoading = true) }
                getMaxDate()
                getGastosProgramados()
                mostrarCurrentMoney()
                mostrarTotalIngresos()
                mostrarTotalGastos()
                mostrarEstadoUsuario()
                listCatGastosNueva()
                listCatIngresosNueva()
                getPreferences()
                getUserData()
                _homeUiState.update { _homeUiState.value.copy(isLoading = false) }
            } catch (e: Exception) {
                _homeUiState.update { it.copy(isError = true) }
                Log.e(tag, "initMostrandoAlUsuario no hay internet: ${e.message}")
            }
        }
    }

    private fun mostrandoAlUsuario(fechaConBarra: String) {
        mostrarFecha(fechaConBarra)
        mostrarDiasRestantes(fechaConBarra)
    }

    private fun mostrarEstadoUsuario() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val datoFecha = withContext(Dispatchers.IO) { dbm.getDate()?.date }
                if (datoFecha != null) {
                    mostrandoAlUsuario(datoFecha)
                    mostrarLimitePorDia()
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
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarCurrentMoney() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dbm.getCurrentMoney()?.money
            _homeUiState.update { _homeUiState.value.copy(dineroActual = data ?: 0.0) }
        }
    }

    private fun mostrarFecha(fecha: String) {
        //fechaConBarra tiene el formato 05/07/2024
        _homeUiState.update {
            _homeUiState.value.copy(fechaElegida = converterFechaPersonalizada(fecha))
        }
        //muestra 05 Jul 2024
    }

    private fun mostrarDiasRestantes(fecha: String) {
        //se manejan con dias con guion por eso hay que parsearlo antes de enviar
        val diasRestantes = getDiasRestantes(fecha)
        _homeUiState.update { _homeUiState.value.copy(diasRestantes = diasRestantes) }
    }

    private fun mostrarTotalIngresos() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) { dbm.getTotalIngresos()?.totalIngresos }
                _homeUiState.update {
                    _homeUiState.value.copy(
                        mostrandoDineroTotalIngresos = data ?: 0.0
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarTotalGastos() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = dbm.getTotalGastos()?.totalGastos
                _homeUiState.update {
                    _homeUiState.value.copy(
                        mostrandoDineroTotalGastos = data ?: 0.0
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error al obtener el dinero total: ${e.message}")
            }
        }
    }

    private fun mostrarLimitePorDia() {
        viewModelScope.launch {
            val data = dbm.getCurrentMoney()?.money
            val date = dbm.getDate()?.date
            //se manejan con dias con guion por eso hay que parsearlo antes de enviar
            val diasRestantes = getDiasRestantes(date)
            val limitePorDia = MathUtils.getLimitePorDia(data ?: 0.0, diasRestantes = diasRestantes)
            _homeUiState.update { _homeUiState.value.copy(limitePorDia = limitePorDia) }
        }
    }


    private fun getMaxDate() {
        viewModelScope.launch {
            //obteniendo fecha guardada maxima por el usuario
            Log.d("getMax", "getMaxDate")
            dataStorePreferences.getFechaMaximoMes().collect { maxDate ->
                Log.d(tag, "getMaxDate: ${maxDate.numeroGuardado}")
                _homeUiState.update { it.copy(selectedOptionFechaMaxima = maxDate.numeroGuardado.toInt()) }
            }
            Log.d(tag, "getMaxDate: ${_homeUiState.value.selectedOptionFechaMaxima}")
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
    fun cantidadIngresada(cantidadIngresada: String, userSelected: Boolean) {
        _homeUiState.update { _homeUiState.value.copy(cantidadIngresada = cantidadIngresada) }
        viewModelScope.launch {
            val (dataCurrentMoney,
                dataTotalGastos,
                dataTotalIngresos
            ) = withContext(Dispatchers.IO) {
                val currentMoneyDeferred = async { dbm.getCurrentMoney() }
                val totalGastosDeferred = async { dbm.getTotalGastos() }
                val totalIngresosDeferred = async { dbm.getTotalIngresos() }

                Triple(
                    currentMoneyDeferred.await(),
                    totalGastosDeferred.await(),
                    totalIngresosDeferred.await()
                )
            }

            calculadoraDialog(
                cantidadIngresada.toDouble(),
                dataCurrentMoney ?: CurrentMoneyModel(money = 0.0, checked = false),
                dataTotalIngresos ?: TotalIngresosModel(totalIngresos = 0.0),
                dataTotalGastos ?: TotalGastosModel(totalGastos = 0.0),
                userSelected
            )
        }
    }

    private fun addDiner(cantidadIngresada: Double, dataTotal: Double): Double {
        val result = BigDecimal(cantidadIngresada + dataTotal)
            .setScale(2, RoundingMode.HALF_EVEN)
        return result.toDouble()
    }

    private fun createOrUpdateCurrentMoney(item: CurrentMoneyModel) {
        viewModelScope.launch { currentMoneyFirestore.createOrUpdate(item) }
    }

    private fun insertPrimerTotalIngresos(item: TotalIngresosModel) {
        viewModelScope.launch {
            totalIngresosFirestore.createOrUpdate(item)
            mostrarTotalIngresos()
        }
    }

    private fun insertUpdateFecha(context: Context, fecha: String) {
        viewModelScope.launch {
            try {
                val date = withContext(Dispatchers.IO) { dbm.getDate() }
                if (date == null) {
                    //si es true se ingresa por primera vez
                    insertDate(DateModel(date = fecha, isSelected = false), fecha)
                } else {
                    //si es false se actualiza en la base de datos y por las dudas se sigue manteniendo que es false
                    updateDate(DateModel(date = fecha, isSelected = false))
                }
            } catch (e: DateTimeParseException) {
                Toast.makeText(
                    context,
                    "Error al analizar la fecha: $fecha en insertUpdateFecha",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(tag, "Error al analizar la fecha: $fecha", e)
            }
        }
    }

    private fun insertDate(item: DateModel, fechaConBarra: String) {
        viewModelScope.launch {
            dateFirestore.createOrUpdate(item) //insertando en la base de datos
            _homeUiState.update { _homeUiState.value.copy(fechaElegida = item.date!!) }
            mostrandoAlUsuario(fechaConBarra)
            mostrarDiasRestantes(item.date!!)
            mostrarLimitePorDia()
        }
    }

    private fun updateDate(item: DateModel) {
        //obteniendo fecha con guion ej 2024-04-02
        viewModelScope.launch(Dispatchers.IO) {
            val document = dateFirestore.get()
            if (document != null) {
                val entity = DateModel(date = item.date, isSelected = false)
                dateFirestore.createOrUpdate(entity)
                mostrandoAlUsuario(entity.date!!)
            } else {
                Log.e(tag, "El objeto nuevaFecha es nulo")
            }
        }
    }

    private fun updateTotalIngresos(item: TotalIngresosModel) {
        viewModelScope.launch {
            totalIngresosFirestore.createOrUpdate(item)
            mostrarCurrentMoney()
            mostrarTotalIngresos()
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

    fun sendDateElegida(context: Context, date: String) {
        try {
            insertUpdateFecha(context, fecha = date)
        } catch (e: ParseException) {
            Toast.makeText(
                context,
                "Error al formatear la fecha en SendDateElegida",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(tag, "Error al formatear la fecha: ${e.message}")
        }
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
        isChecked: Boolean
    ) {
        val day = SimpleDateFormat("dd", Locale.getDefault()).format(Date()).toInt()
        val formattedDay = String.format(Locale.getDefault(), "%02d", day)
        val dateFormat = SimpleDateFormat("$formattedDay MMM yyyy", Locale.getDefault())

        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { dbm.getTransactions() }
            val newIndex = list.maxOfOrNull { it.index ?: 0 }?.plus(1) ?: 0

            transactionsFirestore.create(
                TransactionModel(
                    title = categoryName,
                    subTitle = description,
                    cash = cantidad,
                    select = isChecked,
                    date = dateFormat.format(Date()),
                    icon = categoryIcon.toString(),
                    index = newIndex
                )
            )
        }
        onDialogClose()
        _homeUiState.update { _homeUiState.value.copy(cantidadIngresada = "", description = "") }
    }

    //...FUNCION QUE SE USA PARA CALCULAR TOTALiNGRESOS, TOTALgASTOS, CURENTmONEY
    private fun calculadoraDialog(
        cantidadIngresada: Double,
        data: CurrentMoneyModel,
        dataIngresos: TotalIngresosModel,
        dataGastos: TotalGastosModel,
        userSelected: Boolean
    ) {
        viewModelScope.launch {
            val nuevoTotal = when (userSelected) {
                true -> addDiner(cantidadIngresada, dataIngresos.totalIngresos ?: 0.0)
                else -> addDiner(cantidadIngresada, dataGastos.totalGastos ?: 0.0)
            }

            val dinerActual = when (userSelected) {
                true -> addDiner(cantidadIngresada, data.money ?: 0.0)
                else -> maxOf(restarDinero(cantidadIngresada.toString(), data.money), 0.0)
            }

            //si el usuario eligio ingresos
            when (userSelected) {
                true -> if (data.checked == true) {
                    //data.checked es true entonces significa que no hay nada aun guardado
                    insertPrimerTotalIngresos(TotalIngresosModel(totalIngresos = nuevoTotal))
                } else {
                    //Si el usuario eligio Ingresos pero en data.isChecked es false
                    // significa que ya hay datos guardados, por ende se actualizaran los ingresos
                    // Si ya hay datos en db se actualiza el totalIngresos
                    updateTotalIngresos(TotalIngresosModel(totalIngresos = nuevoTotal))
                }
                //Si el usuario eligio Gastos
                false -> {
                    //si el usuario eligio gastos
                    if (dinerActual == 0.0 && nuevoTotal > 0.0) {
                        //reseteando el progress
                        reseteandoProgress()
                    } else {
                        // Si el usuario eligió gastos, actualizar la base de datos con el nuevo dinero
                        totalGastosFirestore.createOrUpdate(TotalGastosModel(totalGastos = nuevoTotal))
                        mostrarTotalGastos()
                    }
                }
            }
            //actualizando el dinero actual siempre
            createOrUpdateCurrentMoney(CurrentMoneyModel(money = dinerActual, checked = false))
            mostrarCurrentMoney()
            mostrarLimitePorDia()
        }
    }

    fun refreshData(context: Context) {
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
                        isChecked = true,// el menu de ingresos esta activado
                        enabledButtonGastos = false //el boton de gastos esta desactivado
                    )
                }
            } else {
                _homeUiState.update {
                    _homeUiState.value.copy(
                        buttonIngresosActivated = 0,// el boton gastos esta activado
                        isChecked = false,// el menu de gastos esta activado
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

    fun setIsChecked(value: Boolean) {
        _homeUiState.update { _homeUiState.value.copy(isChecked = value) }
    }

    fun setShowNuevoMes(value: Boolean) {
        _homeUiState.update { _homeUiState.value.copy(showNuevoMes = value) }
    }

    private fun getString(idRecurso: Int): String {
        return resources.getString(idRecurso)
    }

    fun getCurrentUser(): FirebaseUser? = authFirebaseImp.getCurrentUser()

    //--------------Gastos programados
    fun pagarItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            try {
                clearItem(item)
                crearTransaction(
                    cantidad = item.cash ?: "",
                    categoryName = item.title ?: "",
                    description = item.subTitle ?: "",
                    categoryIcon = item.icon?.toInt() ?: 0,
                    isChecked = false
                )
                val nuevoMes: String =
                    DateUtils.toLocalDate(item.date ?: "").plusMonths(1).toString()
                gastosProgramadosFirestore.update(item.copy(date = nuevoMes))
            } catch (e: Exception) {
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
                Log.e(tag, "getGastosprogramados: error", e)
            }
        }
    }

    //--------------Gastos programados
    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    fun createPrueba(item: UserPreferences) {
        viewModelScope.launch {
            up.createOrUpdate(item)
        }
    }

    fun getPreferences() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) { up.get() }
                _userPreferences.update {
                    it.copy(
                        dateMax = data?.dateMax,
                        hour = data?.hour,
                        minute = data?.minute,
                        biometricSecurity = data?.biometricSecurity,
                        themeMode = data?.themeMode
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "getPreferences: error", e)
            }
        }
    }

    fun updateBiometricSecurity(value: Boolean) {
        viewModelScope.launch {
            try {
                up.updateBiometricSecurity(value)
            } catch (e: Exception) {
                Log.e(tag, "updateBiometricSecurity: error", e)
            }
        }
    }

    fun updateDateMax(value: Int) {
        viewModelScope.launch {
            try {
                up.updateDateMax(value)
            } catch (e: Exception) {
                Log.e(tag, "updateDateMax: error", e)
            }
        }
    }

    fun updateHourMinute(valueHour: Int, valueMinute: Int) {
        viewModelScope.launch {
            try {
                up.updateHourMinute(valueHour, valueMinute)
            } catch (e: Exception) {
                Log.e(tag, "updateHourMinute: error", e)
            }
        }
    }

    fun updateThemeMode(value: ModeDarkThemeEnum) {
        viewModelScope.launch {
            try {
                up.updateThemeMode(value)
            } catch (e: Exception) {
                Log.e(tag, "updateDarkMode: error", e)
            }
        }
    }

    private val _userData = MutableStateFlow(UserData())
    val userData: StateFlow<UserData> = _userData.asStateFlow()

    fun getUserData() {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) { udb.get() }
                _userData.update {
                    it.copy(
                        totalGastos = data?.totalGastos,
                        totalIngresos = data?.totalIngresos,
                        currentMoney = data?.currentMoney,
                        isCurrentMoneyIngresos = data?.isCurrentMoneyIngresos,
                        selectedDate = data?.selectedDate,
                        isSelectedDate = data?.isSelectedDate
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Error en getUserData", e)
            }
        }
    }

    fun updateCurrentMoney(valueCurrentMoney: Double, valueIsCurrentMoneyIngresos: Boolean) {
        viewModelScope.launch {
            try {
                udb.updateCurrentMoney(valueCurrentMoney, valueIsCurrentMoneyIngresos)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateCurrentMoney", e)
            }
        }
    }
}