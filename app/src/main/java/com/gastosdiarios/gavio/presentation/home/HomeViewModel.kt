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
import com.gastosdiarios.gavio.domain.model.CategoriesModel
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
import com.gastosdiarios.gavio.domain.repository.AuthFirebaseImp
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CreateGastosProgramadosFireStore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CurrentMoneyFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.DateFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalIngresosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.DateUtils.agregandoUnMes
import com.gastosdiarios.gavio.utils.DateUtils.converterFechaABarra
import com.gastosdiarios.gavio.utils.DateUtils.converterFechaAGuion
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
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
    private val gastosProgramadosFirestore: CreateGastosProgramadosFireStore,
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
                        //  parseando fecha a LocalDate
                        val dateDataString = converterFechaAGuion(dataDate)
                        val fechaParseadaAGuion =
                            parsearFechaALocalDate(dateDataString) // muestra 2024-04-30

                        // ej: 2023-12-12
                        //si la fecha actual es igual que la fecha guardada
                        if (fechaActual >= fechaParseadaAGuion) {
                            if (dataCurrentMoney == 0.0) {
                                updateFechaUnMesMas(fechaActual, fechaParseadaAGuion)
                                _homeUiState.update { it.copy(showNuevoMes = true) }
                            } else if (dataCurrentMoney != 0.0) {
                                //si aun tiene dinero el usuario al finalizar la fecha elegida
                                updateFechaUnMesMas(fechaActual, fechaParseadaAGuion)
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
                mostrarCurrentMoney()
                mostrarTotalIngresos()
                mostrarTotalGastos()
                mostrarEstadoUsuario()
                listCatGastosNueva()
                listCatIngresosNueva()
                getGastosProgramados()
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
                            fechaElegidaBarra = "",
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

    private fun mostrarFecha(fechaConBarra: String) {
        //fechaConBarra tiene el formato 05/07/2024
        _homeUiState.update {
            _homeUiState.value.copy(
                fechaElegidaBarra = converterFechaPersonalizada(
                    fechaConBarra
                )
            )
        }
        //muestra 05 Jul 2024
    }

    private fun mostrarDiasRestantes(fechaConBarra: String) {
        val fechaConGuion = converterFechaAGuion(fechaConBarra)
        //se manejan con dias con guion por eso hay que parsearlo antes de enviar
        val diasRestantes = getDiasRestantes(fechaConGuion)
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
            val fechaConGuion = date?.let { converterFechaAGuion(it) }
            //se manejan con dias con guion por eso hay que parsearlo antes de enviar
            val diasRestantes = getDiasRestantes(fechaConGuion)
            val limitePorDia = MathUtils.getLimitePorDia(data ?: 0.0, diasRestantes = diasRestantes)
            _homeUiState.update { _homeUiState.value.copy(limitePorDia = limitePorDia) }
        }
    }


    private fun getMaxDate() {
        viewModelScope.launch {
            //obteniendo fecha guardada maxima por el usuario
            dataStorePreferences.getFechaMaximoMes().collect { maxDate ->
                _homeUiState.update { it.copy(selectedOptionFechaMaxima = maxDate.numeroGuardado.toInt()) }
            }
        }
    }

    private fun getDiasRestantes(getFechaGuion: String?): Int {
        if (getFechaGuion.isNullOrEmpty()) {
            return 0
        }
        val fLocalDate = parsearFechaALocalDate(getFechaGuion)
        val fechaActual = obtenerFechaActual()
        val dia: Long = ChronoUnit.DAYS.between(fechaActual, fLocalDate)
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

    private fun insertUpdateFecha(fechaConBarra: String) {
        viewModelScope.launch {
            try {
                val date = withContext(Dispatchers.IO) { dbm.getDate() }
                if (date == null) {
                    //si es true se ingresa por primera vez
                    insertDate(
                        DateModel(date = fechaConBarra, isSelected = false),
                        fechaConBarra
                    )
                } else {
                    //si es false se actualiza en la base de datos y por las dudas se sigue manteniendo que es false
                    updateDate(DateModel(date = fechaConBarra, isSelected = false))
                }
            } catch (e: DateTimeParseException) {
                Log.e(tag, "Error al analizar la fecha: $fechaConBarra", e)
            }
        }
    }

    private fun insertDate(item: DateModel, fechaConBarra: String) {
        viewModelScope.launch {
            dateFirestore.createOrUpdate(item) //insertando en la base de datos
            _homeUiState.update { _homeUiState.value.copy(fechaElegidaBarra = item.date!!) }
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
        _homeUiState.update { _homeUiState.value.copy(fechaElegidaBarra = "", diasRestantes = 0) }
    }

    fun sendDateElegida(date: String) {
        // date me pasa ej 17/7/2024 pasarla a 17/07/2024
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val dateFormat: Date? = sdf.parse(date)
            val formattedDate: String = sdf.format(dateFormat!!)
            insertUpdateFecha(fechaConBarra = formattedDate)
        } catch (e: ParseException) {
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

    fun isDateSelectable(utcTimeMillis: Long, maxDates: Int): Boolean {
        val now = obtenerFechaActual()
        val diasMaximos = TimeUnit.DAYS.toMillis(maxDates.toLong())
        val minDate = now.atTime(0, 0, 0, 0).toEpochSecond(ZoneOffset.UTC) * 1000
        val maxDate = minDate + diasMaximos
        return utcTimeMillis in minDate..maxDate
    }

    fun formatSelectedDate(selectedDateMillis: Long?): String {
        return selectedDateMillis?.let {
            val zonaHoraria = ZoneId.systemDefault()
            val localDate = Instant.ofEpochMilli(it).atZone(zonaHoraria).toLocalDate()
            "${localDate.dayOfMonth + 1}/${localDate.monthValue}/${localDate.year}"
        } ?: ""
    }

    fun crearTransaction(
        cantidad: String,
        categoryName: String,
        description: String,
        categoryIcon: Int,
        isChecked: Boolean
    ) {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) { dbm.getTransactions() }
            val newIndex = list.maxOfOrNull { it.index ?: 0 }?.plus(1) ?: 0
            val currentDate = obtenerFechaActual().toString()

            transactionsFirestore.create(
                TransactionModel(
                    title = categoryName,
                    subTitle = description,
                    cash = cantidad,
                    select = isChecked,
                    date = currentDate,
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


    //metodo que devuelve un true para que abra el dialogo de transaccion
    fun onShowDialogClickTransaction() {
        viewModelScope.launch {
            _homeUiState.update { _homeUiState.value.copy(agregar = true) }
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
                agregar = false,
                cantidadIngresada = "",
                description = ""
            )
        }
    }

    fun onDismiss() {
        _homeUiState.update {
            _homeUiState.value.copy(
                editar = false,
                cantidadIngresada = ""
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

    fun resetErrorState() {
        _homeUiState.update { it.copy(isError = false) }
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

    //--------------para manejar la lista filttrada de gastos programados------//
    private fun getGastosProgramados() {
        viewModelScope.launch {
            val currentDate = DateUtils.obtenerFechaActual().toString()
            val data: List<GastosProgramadosModel> = gastosProgramadosFirestore.get()

            val listFilter = data
                .filter { item -> item.date == currentDate || item.date!! < currentDate }

            if (listFilter.isNotEmpty()) {
                _listFilter.addAll(listFilter)
            } else {
                _listFilter.addAll(emptyList())
            }
        }
    }

    fun pagarItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            try {
                removeItem(item)
                //despues de remover el item para una mejor experiencia de usuario
                // que se le elimine rapido de la pantalla,
                // se resuelve lo demas
                val list = withContext(Dispatchers.IO) { dbm.getTransactions() }
                val newIndex = list.maxOfOrNull { it.index ?: 0 }?.plus(1) ?: 0
                val currentDate = obtenerFechaActual().toString()

                transactionsFirestore.create(
                    TransactionModel(
                        title = item.title,
                        subTitle = item.subTitle,
                        cash = item.cash,
                        select = false, //significa gastos
                        date = currentDate,
                        icon = item.icon.toString(),
                        index = newIndex
                    )
                )

                crearNuevaCategoriaDeGastos(
                    item.title ?: "",
                    item.icon?.toInt() ?: 0,
                    item.cash ?: ""
                )

                updateDate(item)
            } catch (e: Exception) {
                Log.e("Error al pagar item", e.message.toString())
            }
        }
    }

    fun clearItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            try {
                removeItem(item)
                updateDate(item)

            } catch (e: DateTimeParseException) {
                Log.e("ClearItem", "Error parsing date: ${e.message}")
            } catch (e: Exception) {
                Log.e("ClearItem", "Error updating database: ${e.message}")
            }
        }
    }

    private fun updateDate(item: GastosProgramadosModel) {
        viewModelScope.launch {
            val itemDate = LocalDate.parse(item.date)
            val updatedItem = item.copy(
                date = itemDate.plusMonths(1).toString()
            )
            gastosProgramadosFirestore.update(updatedItem)
        }
    }

    private fun removeItem(item: GastosProgramadosModel) {
        val uidEncontrado = _listFilter.find { it.uid == item.uid }
        _listFilter.remove(uidEncontrado)
    }

    //--------------fin la lista filttrada de gastos programados------//
}