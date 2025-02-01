package com.gastosdiarios.gavio.presentation.transaction

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.model.DataList
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserDataFirestore
import com.gastosdiarios.gavio.utils.MathUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.wait
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

    private val _uiState = MutableStateFlow<UiStateList<TransactionModel>>(UiStateList.Loading)
    val uiState = _uiState.onStart { getAllTransactions() }
        .catch { throwable ->
            _uiState.update {
                UiStateList.Error(
                    throwable.message ?: "Error desconocido",
                    throwable
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    val snackbarMessage: StateFlow<Int?> get() = _snackbarMessage

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _dataList = MutableStateFlow(DataList<TransactionModel>())
    val dataList: StateFlow<DataList<TransactionModel>> = _dataList.asStateFlow()


    private fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data: List<TransactionModel> = dbm.getTransactions()
                if (data.isEmpty()) {
                    _uiState.update { UiStateList.Empty }
                } else {
                    _uiState.update { UiStateList.Success(data) }
                }
            } catch (e: Exception) {
                _uiState.update { UiStateList.Error(e.message ?: "Error desconocido", e) }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data: List<TransactionModel> = dbm.getTransactions()
                if (data.isEmpty()) {
                    _uiState.update { UiStateList.Empty }
                } else {
                    _uiState.update { UiStateList.Success(data) }
                }
            } catch (e: Exception) {
                _uiState.update { UiStateList.Error(e.message ?: "Error desconocido", e) }
            }
        }
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
                    _dataList.update { it.copy(selectionMode = false) }
                    dbm.deleteAllScreenTransactions()
                    dbm.updateCurrentMoney(0.0, true)
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
        item: TransactionModel
    ) {
        viewModelScope.launch {
            try {
                //obteniendo el total de ingresos y gastos
                val data = userDataFirestore.get()
                val cashViejo = item.cash?.toDouble() ?: 0.0
                val dataTotalIngresos = data?.totalIngresos ?: 0.0
                val dataTotalGastos = data?.totalGastos ?: 0.0

                if (item.tipoTransaccion == TipoTransaccion.GASTOS && nuevoValor.toDouble() > dataTotalIngresos) {
                    _snackbarMessage.value =
                        R.string.error_el_gasto_no_puede_ser_superior_a_los_ingresos

                } else if (dataTotalGastos == dataTotalIngresos) {
                    dbm.deleteAllScreenTransactions()
                    cargandoListaActualizada()
                    return@launch
                } else {
                    when (item.tipoTransaccion) {
                        //si es true estamos actualizando dinero de tipo ingresos
                        TipoTransaccion.INGRESOS -> {
                            // Ajustar el total de ingresos según si el nuevo valor es mayor o menor al anterior.
                            if (nuevoValor > item.cash.toString()) {
                                val diferencia =
                                    nuevoValor.toDouble().minus(item.cash?.toDouble() ?: 0.0)
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
                                    cashViejo.minus(nuevoValor.toDouble())
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
                            if (nuevoValor > cashViejo.toString()) {
                                val diferencia =
                                    nuevoValor.toDouble().minus(cashViejo)
                                val totalGastos =
                                    MathUtils.sumarBigDecimal(dataTotalGastos, diferencia)
                                val currentMoney =
                                    MathUtils.restarBigDecimal(dataTotalIngresos, totalGastos)
                                updateCurrentMoney(currentMoney)
                                updateTotalGastos(totalGastos)
                            } else {
                                val diferencia =
                                    cashViejo.minus(nuevoValor.toDouble())
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
                    // Actualiza el valor en la lista
                    // si es un ingreso o si el nuevo valor no es superior al total de ingresos
                    Log.d(tag, "updateItem: ${nuevoValor}")
                    updateItemList(
                        item.copy(title = title, subTitle = description, cash = nuevoValor)
                    )
                    //actualizando el gastos por categorias
                    updateGastosCategory(title, nuevoValor, item)
                    cargandoListaActualizada()
                }

            } catch (e: Exception) {
                Log.d(tag, "Error en updateItem: ${e.message}")
            }
        }
    }

    private fun cargandoListaActualizada() {
        _dataList.update { it.copy(updateItem = true) }
        viewModelScope.launch {
            val data = dbm.getTransactions()
            if (data.isEmpty()) {
                _uiState.update { UiStateList.Empty }
            } else {
                _dataList.update { it.copy(updateItem = false) }
                _uiState.update { UiStateList.Success(data) }
            }
        }
    }

    //.....................................................................................................


    private fun updateCurrentMoney(nuevoDinero: Double) {
        viewModelScope.launch {
            try {
                userDataFirestore.updateCurrentMoney(nuevoDinero, false)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar el dinero", Toast.LENGTH_SHORT).show()
                Log.e(tag, " Error en updateCurrentMoney: ${e.message}")
            }
        }
    }

    private fun updateTotalIngresos(totalIngresos: Double) {
        viewModelScope.launch {
            try {
                userDataFirestore.updateTotalIngresos(totalIngresos)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar el total Ingresos", Toast.LENGTH_SHORT)
                    .show()
                Log.e(tag, " Error en updateTotalIngresos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA ACTUALIZAR EL TOTAL GASTOS AL EDITAR UN GASTO
    private fun updateTotalGastos(totalGastos: Double) {
        viewModelScope.launch {
            try {
                //si el usuario edita un gasto se actualiza el total de gastos
                userDataFirestore.updateTotalGastos(totalGastos)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar el total Gastos", Toast.LENGTH_SHORT)
                    .show()
                Log.e(tag, "Error en updateTotalGastos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA EDITAR UN ITEM DE LA LISTA
    private fun updateItemList(item: TransactionModel) {
        viewModelScope.launch {
            try {
                transactionsFirestore.update(item).wait()

                // Obtener la lista actual del uiState
                val currentUiState = _uiState.value
                if (currentUiState is UiStateList.Success) {
                    val currentData = currentUiState.data.toMutableList()

                    // Buscar el índice del elemento a actualizar
                    val index = currentData.indexOfFirst { it.uid == item.uid }

                    if (index != -1) {
                        // Actualizar el elemento en la lista
                        currentData[index] = item
                        // Actualizar el uiState con la lista modificada
                        _uiState.update { UiStateList.Success(currentData) }
                    }
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