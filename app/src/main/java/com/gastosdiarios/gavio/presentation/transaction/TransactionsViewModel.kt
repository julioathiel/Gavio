package com.gastosdiarios.gavio.presentation.transaction

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
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
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDataFirestore: UserDataFirestore,
    private val transactionsFirestore: TransactionsFirestore,
    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val dbm: DataBaseManager
) : ViewModel() {
    private val tag = "transactionViewModel"

    data class DataList<T>(
        val selectedItems: List<T> = emptyList(),
        val expandedItem: T? = null,
        val selectionMode: Boolean = false,
        val isCreate: Boolean = false,
        val isDelete: Boolean = false
    )

    private val _uiState = MutableStateFlow(ListUiState<TransactionModel>())
    val uiState: StateFlow<ListUiState<TransactionModel>> =
        _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    val snackbarMessage: StateFlow<Int?> get() = _snackbarMessage

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _dataList = MutableStateFlow(DataList<TransactionModel>())
    val dataList: StateFlow<DataList<TransactionModel>> = _dataList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.onStart { getAllTransactions() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )


    private fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val data: List<TransactionModel> = dbm.getTransactions()
            _uiState.update { it.copy(items = data) }
            _loading.value = false
        }
    }

    fun refreshData() {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                val data: List<TransactionModel> = dbm.getTransactions()
                _uiState.update { it.copy(items = data) }
            }
        )
    }


    private fun onItemRemoveMov() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dbm.getUserData()
            val currentMoney = data?.currentMoney ?: 0.0
            val totalIngresos = data?.totalIngresos ?: 0.0
            val totalGastos = data?.totalGastos ?: 0.0


            val sumTotalIngresos =
                _dataList.value.selectedItems.filter { it.tipoTransaccion == TipoTransaccion.INGRESOS }
                    .sumOf { it.cash?.toDouble() ?: 0.0 }
            val sumTotalGastos =
                _dataList.value.selectedItems.filter { it.tipoTransaccion == TipoTransaccion.GASTOS }
                    .sumOf { it.cash?.toDouble() ?: 0.0 }

            //nuevo currentMoney
            val updatedMoney = currentMoney - sumTotalIngresos + sumTotalGastos

            _dataList.value.selectedItems.forEach { item ->
                val esPrimerItem = item.index == 0
                if (esPrimerItem) {
                    dbm.deleteAllScreenTransactions()
                    dbm.updateCurrentMoney(updatedMoney, true)
                    cargandoListaActualizada()
                    return@launch
                }
                dbm.deleteTransaction(item)
                if (item.tipoTransaccion == TipoTransaccion.GASTOS) {
                    deleteGastosPorCategoria(item.title.orEmpty())
                }
            }

            dbm.updateCurrentMoney(updatedMoney, false)

            updateTotalIngresos(totalIngresos.minus(sumTotalIngresos))
            updateTotalGastos(totalGastos.minus(sumTotalGastos))
            cargandoListaActualizada()
        }
    }

    fun updateItem(
        title: String,
        nuevoValor: String,
        description: String,
        valorViejo: TransactionModel
    ) {
        viewModelScope.launch {
            try {
                //obteniendo el total de ingresos y gastos
                val data = userDataFirestore.get()
                val cashViejo = valorViejo.cash?.toDouble()!!
                val dataTotalIngresos = data?.totalIngresos ?: 0.0
                val dataTotalGastos = data?.totalGastos ?: 0.0

                val totalGastosSinElementoActual = dataTotalGastos - cashViejo

                // Verificar si el nuevo valor del gasto más el total de los demás gastos iguala al total de ingresos
                if (nuevoValor.toDouble() + totalGastosSinElementoActual == dataTotalIngresos) {
                    dbm.deleteAllScreenTransactions()
                    cargandoListaActualizada()
                    return@launch // Salir de la función después de eliminar las transacciones
                }

                if (valorViejo.tipoTransaccion == TipoTransaccion.GASTOS && nuevoValor.toDouble() > dataTotalIngresos) {
                    _snackbarMessage.value =
                        R.string.error_el_gasto_no_puede_ser_superior_a_los_ingresos

                } else if (dataTotalGastos == dataTotalIngresos) {
                    dbm.deleteAllScreenTransactions()
                    cargandoListaActualizada()
                } else {
                    // Actualiza el valor en la lista
                    // si es un ingreso o si el nuevo valor no es superior al total de ingresos
                    updateItemList(
                        TransactionModel(
                            uid = valorViejo.uid,
                            title = title,
                            subTitle = description,
                            cash = nuevoValor,
                            tipoTransaccion = valorViejo.tipoTransaccion,
                            date = valorViejo.date,
                            icon = valorViejo.icon,
                            index = valorViejo.index
                        )
                    )
                    //actualizando el gastos por categorias
                    updateGastosCategory(title, nuevoValor, valorViejo)

                    when (valorViejo.tipoTransaccion) {
                        //si es true estamos actualizando dinero de tipo ingresos
                        TipoTransaccion.INGRESOS -> {
                            // Ajustar el total de ingresos según si el nuevo valor es mayor o menor al anterior.
                            if (nuevoValor > valorViejo.cash) {
                                val diferencia =
                                    nuevoValor.toDouble().minus(valorViejo.cash.toDouble())
                                val nuevoTotalIngresos = dataTotalIngresos.plus(diferencia)
                                val currentMoney = if (dataTotalGastos != 0.0) {
                                    MathUtils.restarBigDecimal(nuevoTotalIngresos, dataTotalGastos)
                                } else {
                                    MathUtils.sumarBigDecimal(dataTotalIngresos, diferencia)
                                }
                                val totalIngresos =
                                    MathUtils.bigDecimalToDouble(nuevoTotalIngresos)
                                updateCurrentMoney(currentMoney)
                                updateTotalIngresos(totalIngresos)
                            } else {
                                val diferencia =
                                    valorViejo.cash.toDouble().minus(nuevoValor.toDouble())
                                val nuevoTotalIngresos = dataTotalIngresos.minus(diferencia)
                                val currentMoney = if (dataTotalGastos != 0.0) {
                                    MathUtils.restarBigDecimal(
                                        nuevoTotalIngresos,
                                        dataTotalGastos
                                    )
                                } else {
                                    MathUtils.restarBigDecimal(dataTotalIngresos, diferencia)
                                }
                                val totalIngresos =
                                    MathUtils.bigDecimalToDouble(nuevoTotalIngresos)
                                updateCurrentMoney(currentMoney)
                                updateTotalIngresos(totalIngresos)
                            }
                        }

                        TipoTransaccion.GASTOS -> {
                            //si es false estamos actualizando dinero de tipo gastos
                            if (nuevoValor > valorViejo.cash) {
                                val diferencia =
                                    nuevoValor.toDouble().minus(valorViejo.cash.toDouble())
                                val totalGastos =
                                    MathUtils.sumarBigDecimal(dataTotalGastos, diferencia)
                                val currentMoney =
                                    MathUtils.restarBigDecimal(dataTotalIngresos, totalGastos)
                                updateCurrentMoney(currentMoney)
                                updateTotalGastos(totalGastos)
                            } else {
                                val diferencia =
                                    valorViejo.cash.toDouble().minus(nuevoValor.toDouble())
                                val totalGastos =
                                    MathUtils.restarBigDecimal(dataTotalGastos, diferencia)
                                val currentMoney =
                                    MathUtils.restarBigDecimal(dataTotalIngresos, totalGastos)
                                updateCurrentMoney(currentMoney)
                                updateTotalGastos(totalGastos)
                            }
                        }

                        null -> {
                            Log.i(tag, "updateItem: null")
                        }
                    }
                }

            } catch (e: Exception) {
                Log.d(tag, "Error en updateItem: ${e.message}")
            }
        }
    }

    private fun cargandoListaActualizada() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(update = true) }
            val data = dbm.getTransactions()
            _uiState.update { it.copy(items = data, update = false) }
        }
    }

    //..................................funciones que se usan para eliminar un item........................
    //eliminando un ingreso
    private fun updateIngresosTotales(totalIngresos: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val totalIngreso = userDataFirestore.get()?.totalIngresos ?: 0.0
                val nuevoValor = totalIngreso.minus(totalIngresos)
                userDataFirestore.updateTotalIngresos(nuevoValor)
            } catch (e: Exception) {
                Log.e(
                    tag,
                    "Error en updateIngresosTotales: No se pudo obtener los datos de los ingresos ${e.message}"
                )
            }
        }
    }

    //eliminando un gasto
    private fun updateGastos(totalGastos: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val totalGasto = userDataFirestore.get()?.totalGastos ?: 0.0
                val nuevoValor = totalGasto.minus(totalGastos)
                userDataFirestore.updateTotalGastos(nuevoValor)
            } catch (e: Exception) {
                Log.e(
                    tag,
                    "Error en updateGastos: No se pudo obtener los datos de los gastos ${e.message}"
                )
            }
        }
    }

    //.....................................................................................................

    private fun updateCurrentMoneyList(
        listTransactions: List<TransactionModel>,
        cash: Double, isCurrentMoneyIngresos: Boolean,
    ) {
        viewModelScope.launch {
            val currentMoney = userDataFirestore.get()?.currentMoney ?: 0.0
            if (listTransactions.size == 1) {
                // Si es el último elemento, establecer el saldo directamente a 0.0
                userDataFirestore.updateCurrentMoney(0.0, isCurrentMoneyIngresos)
                val newValue = currentMoney + cash
                userDataFirestore.updateCurrentMoney(maxOf(newValue, 0.0), isCurrentMoneyIngresos)
            } else {
                Log.d("tagss", "updateCurrentMoneyList: $cash")
                val newValue = currentMoney + cash
                userDataFirestore.updateCurrentMoney(newValue, isCurrentMoneyIngresos)
            }
        }
    }

    private fun updateCurrentMoney(nuevoDinero: Double) {
        viewModelScope.launch {
            try {
                userDataFirestore.updateCurrentMoney(nuevoDinero, false)
            } catch (e: Exception) {
                Log.e(tag, " Error en updateCurrentMoney: ${e.message}")
            }
        }
    }

    private fun updateTotalIngresos(totalIngresos: Double) {
        viewModelScope.launch {
            try {
                userDataFirestore.updateTotalIngresos(totalIngresos)
                cargandoListaActualizada()
            } catch (e: Exception) {
                Log.e(tag, " Error en updateTotalIngresos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA ACTUALIZAR EL TOTAL GASTOS AL EDITAR UN GASTO
    private fun updateTotalGastos(totalGastos: Double) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                //si el usuario edita un gasto se actualiza el total de gastos
                userDataFirestore.updateTotalGastos(totalGastos)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateTotalGastos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA EDITAR UN ITEM DE LA LISTA
    private fun updateItemList(item: TransactionModel) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val data = withContext(Dispatchers.IO) { dbm.getTransactions() }

                val itemExisting = data.find { it.uid == item.uid }

                if (itemExisting != null) {
                    transactionsFirestore.update(item)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error en updateItemList: ${e.message}")
            }
        }
    }

    private fun updateGastosCategory(title: String, cash: String, itemModel: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dbm.getGastosPorCategoria()

            val categoryToUpdate = data.find { it.title == title }
            if (categoryToUpdate != null) {
                val oldTotalCash = categoryToUpdate.totalGastado ?: 0.0
                val oldItemCash = itemModel.cash?.toDouble() ?: 0.0
                val newCashDouble = cash.toDouble()
                val residuo = oldItemCash - newCashDouble
                val nuevoTotal = oldTotalCash - residuo
                val updatedCategory = categoryToUpdate.copy(totalGastado = nuevoTotal)
                gastosPorCategoriaFirestore.update(updatedCategory)
            }
        }
    }

    private fun deleteGastosPorCategoria(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dbm.getGastosPorCategoria()

            val categoryToDelete = data.find { it.title == title }
            if (categoryToDelete != null) {
                gastosPorCategoriaFirestore.delete(categoryToDelete)
            }
        }
    }

    fun resetSnackbarMessage() {
        _snackbarMessage.value = null
    }


    fun onClick(item: TransactionModel) {
        _dataList.update { currentDataList ->
            if (currentDataList.selectionMode) {
                val updatedSelectedItems = currentDataList.selectedItems.toMutableList()
                if (updatedSelectedItems.any { it.uid == item.uid }) {
                    updatedSelectedItems.removeAll { it.uid == item.uid }
                } else {
                    updatedSelectedItems.add(item)
                }
                currentDataList.copy(
                    selectedItems = updatedSelectedItems,
                    selectionMode = updatedSelectedItems.isNotEmpty()
                )
            } else {
                currentDataList.copy(expandedItem = if (currentDataList.expandedItem == item) null else item)
            }
        }
    }

    fun onLongClick(item: TransactionModel) {
        _dataList.update { currentDataList ->
            val updatedSelectedItems = currentDataList.selectedItems.toMutableList()
            if (updatedSelectedItems.any { it.uid == item.uid }) {
                updatedSelectedItems.removeAll { it.uid == item.uid }
            } else {
                updatedSelectedItems.add(item)
            }
            currentDataList.copy(selectedItems = updatedSelectedItems, selectionMode = true)
        }
    }

    fun isCreateTrue() {
        _dataList.update { it.copy(isCreate = true) }
    }

    fun isCreateFalse() {
        _dataList.update { it.copy(isCreate = false) }
    }

    fun isDeleteTrue() {
        _dataList.update { it.copy(isDelete = true) }
    }

    fun isDeleteFalse() {
        _dataList.update { it.copy(isDelete = false) }
    }

    fun deleteItemSelected() {
        viewModelScope.launch {
            _dataList.update { it.copy(selectionMode = false) }
            onItemRemoveMov()
        }
    }

    fun delete(item: TransactionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                transactionsFirestore.delete(item)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                Log.e("Error", e.message.toString())
            }
        }
    }

    fun clearSelection(item: TransactionModel) {
        isCreateFalse()
        _dataList.update { it.copy(selectionMode = false, selectedItems = emptyList()) }
        onClick(item)
    }
}