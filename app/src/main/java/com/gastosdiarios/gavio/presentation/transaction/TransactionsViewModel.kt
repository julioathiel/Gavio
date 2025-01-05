package com.gastosdiarios.gavio.presentation.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.events_handlers.OnActionsMovimientos
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.data.ui_state.TransactionsUiState
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.CurrentMoneyModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalGastosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TotalIngresosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CurrentMoneyFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosPorCategoriaFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TotalIngresosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.TransactionsFirestore
import com.gastosdiarios.gavio.utils.MathUtils
import com.gastosdiarios.gavio.utils.RefreshDataUtils
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val currentMoneyFirestore: CurrentMoneyFirestore,
    private val totalIngresosFirestore: TotalIngresosFirestore,
    private val totalGastosFirestore: TotalGastosFirestore,
    private val transactionsFirestore: TransactionsFirestore,
    private val gastosPorCategoriaFirestore: GastosPorCategoriaFirestore,
    private val dbm: DataBaseManager
) : ViewModel() {
    private val tag = "transactionViewModel"

    private val _transactionUiState = MutableStateFlow(ListUiState<TransactionModel>())
    val transactionUiState: StateFlow<ListUiState<TransactionModel>> =
        _transactionUiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<Int?>(null)
    val snackbarMessage: StateFlow<Int?> get() = _snackbarMessage

    private val _interactionState = MutableStateFlow(TransactionsUiState())
    val interactionState: StateFlow<TransactionsUiState> = _interactionState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()


    val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.onStart {
        getAllTransactions()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)


     fun getAllTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactionUiState.update { it.copy(isLoading = true) }
            val data: List<TransactionModel> = dbm.getTransactions()
            _transactionUiState.update {
                it.copy(items = data, isLoading = false)
            }
        }
    }

    fun refreshData() {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                val data: List<TransactionModel> = dbm.getTransactions()
                _transactionUiState.update {
                    it.copy(items = data)
                }
            }
        )
    }

    fun onEventHandler(e: OnActionsMovimientos) {
        when (e) {
            is OnActionsMovimientos.DeleteItem -> onItemRemoveMov(e.lisTransactions, e.item)
            is OnActionsMovimientos.EditItem -> updateItem(
                e.title,
                e.nuevoValor,
                e.nuevaDescription,
                e.item
            )
        }
    }


    private fun onItemRemoveMov(
        list: List<TransactionModel>,
        item: TransactionModel
    ) {
        // Obtener el tipo de transacción (ingreso o gasto)
        val tipoTransaction = item.select

        viewModelScope.launch {
            // Verificar si es el último elemento de la lista
            val esUltimoItem = list.size == 1
            // Verificar si es el primer elemento de la lista
            val esPrimerItem = item.index == 0

            // Eliminar la transacción
            if (esUltimoItem || esPrimerItem) {
                // Si es el último elemento, update el saldo a 0 y eliminar la transacción
                dbm.deleteAllScreenTransactions()
                cargandoListaActualizada()
                return@launch
            } else {
                //actualizando el gastos por categorias
                deleteGastosPorCategoria(item.title.orEmpty())
                // Si no es el último elemento, eliminar la transacción y update el saldo según el tipo
                dbm.deleteTransaction(item)

                when (tipoTransaction) {
                    //si la transaccion fue de ingresos, se volvera a
                    // update el dinero total, restandolo de nuevo
                    true -> {
                        updateCurrentMoneyList(
                            list,
                            CurrentMoneyModel(
                                money = -item.cash?.toDouble()!!,
                                checked = false
                            )
                        )
                        //    updateIngresosTotales(TotalIngresosModel(totalIngresos = item.cash.toDouble()))
                        updateIngresosTotales(
                            TotalIngresosModel(totalIngresos = item.cash.toDouble())
                        )
                    }
                    //si la transaccion fue de gastos, se volvera a
                    // actualizar  el dinero total, sumandolo de nuevo
                    false -> {
                        updateCurrentMoneyList(
                            list,
                            CurrentMoneyModel(
                                money = item.cash?.toDouble()!!,
                                checked = false
                            )
                        )
                        updateGastos(
                            TotalGastosModel(
                                userId = item.uid,
                                totalGastos = item.cash.toDouble()
                            )
                        )
                    }

                    null -> {}
                }
            }
            cargandoListaActualizada()
        }
    }

    private fun updateItem(
        title: String,
        nuevoValor: String,
        nuevaDescription: String,
        item: TransactionModel
    ) {
        viewModelScope.launch {
            //obteniendo el total de ingresos y gastos
            val dataTotalGastos = dbm.getTotalGastos()?.totalGastos ?: 0.0
            val dataTotalIngresos = dbm.getTotalIngresos()?.totalIngresos ?: 0.0

            if (nuevoValor == item.cash) {
                // El valor en efectivo no ha cambiado, solo actualizar la descripción ya sea de ingresos o gastos
                updateItemList(item.copy(subTitle = nuevaDescription))
                cargandoListaActualizada()
                return@launch // Detener la ejecución aquí
            }
            if (item.select == false && nuevoValor.toDouble() > dataTotalIngresos) {
                _snackbarMessage.value =
                    R.string.error_el_gasto_no_puede_ser_superior_a_los_ingresos

            } else {
                //actualiza la lista independientemente de si es ingreso o gasto
                //mas alla de cualquier edicion asi el usuario ve la lista actualizada con los cambio
                updateItemList(
                    TransactionModel(
                        uid = item.uid,
                        title = item.title,
                        subTitle = nuevaDescription,
                        cash = nuevoValor,
                        select = item.select,
                        date = item.date,
                        icon = item.icon,
                        index = item.index
                    )
                )
                //actualizando el gastos por categorias
                updateGastosCategory(title, nuevoValor, item)

                when (item.select) {
                    //si es true estamos actualizando dinero de tipo ingresos
                    true -> {
                        // Ajustar el total de ingresos según si el nuevo valor es mayor o menor al anterior.
                        if (nuevoValor > item.cash!!) {
                            Log.d(tag, "updateItem: INGRESOS")
                            Log.d(tag, "updateItem: nuevoValor > valorViejo.cash!!")
                            val diferencia = MathUtils.minus(nuevoValor, item.cash)

                            val nuevoTotalIngresos = dataTotalIngresos.plus(diferencia)

                            val currentMoney = if (dataTotalGastos != 0.0) {
                                MathUtils.restarBigDecimal(
                                    nuevoTotalIngresos,
                                    dataTotalGastos
                                )
                            } else {
                                MathUtils.sumarBigDecimal(dataTotalIngresos, diferencia)
                            }

                            val totalIngresos =
                                MathUtils.bigDecimalToDouble(nuevoTotalIngresos)

                            updateCurrentMoney(
                                CurrentMoneyModel(money = currentMoney, checked = false)
                            )

                            updateTotalIngresos(
                                TotalIngresosModel(totalIngresos = totalIngresos)
                            )

                        } else {
                            Log.d(tag, "updateItem: INGRESOS")
                            Log.d(tag, "updateItem: nuevoValor < valorViejo.cash")
                            val diferencia = MathUtils.minus(item.cash, nuevoValor)
                            val nuevoTotalIngresos = dataTotalIngresos.minus(diferencia)

                            val currentMoney = if (dataTotalGastos != 0.0) {
                                MathUtils.restarBigDecimal(nuevoTotalIngresos, dataTotalGastos)
                            } else {
                                MathUtils.restarBigDecimal(dataTotalIngresos, diferencia)
                            }
                            val totalIngresos =
                                MathUtils.bigDecimalToDouble(nuevoTotalIngresos)

                            updateCurrentMoney(
                                CurrentMoneyModel(money = currentMoney, checked = false)
                            )

                            updateTotalIngresos(
                                TotalIngresosModel(totalIngresos = totalIngresos)
                            )
                        }
                    }

                    false -> {
                        //si es false estamos actualizando dinero de tipo gastos
                        if (nuevoValor > item.cash!!) {
                            Log.d(tag, "updateItem: GASTOS")
                            Log.d(tag, "updateItem: nuevoValor > valorViejo.cash")
                            val diferencia = MathUtils.minus(nuevoValor, item.cash)
                            val totalGastos =
                                MathUtils.sumarBigDecimal(dataTotalGastos, diferencia)

                            val currentMoney =
                                MathUtils.restarBigDecimal(dataTotalIngresos, totalGastos)

                            updateCurrentMoney(
                                CurrentMoneyModel(money = currentMoney, checked = false)
                            )

                            updateTotalGastos(TotalGastosModel(totalGastos = totalGastos))

                        } else {
                            Log.d(tag, "updateItem: GASTOS")
                            Log.d(tag, "updateItem: nuevoValor < valorViejo.cash")
                            val diferencia = MathUtils.minus(item.cash, nuevoValor)

                            val totalGastos =
                                MathUtils.restarBigDecimal(dataTotalGastos, diferencia)
                            val currentMoney =
                                MathUtils.restarBigDecimal(dataTotalIngresos, totalGastos)
                            // updateCurrentMoney(currentMoney)
                            Log.d(tag, "updateItem: currentMoney: $currentMoney")
                            Log.d(tag, "updateItem: item.cash: ${item.cash}")
                            updateCurrentMoney(
                                CurrentMoneyModel(money = currentMoney, checked = false)
                            )

                            updateTotalGastos(TotalGastosModel(totalGastos = totalGastos))
                        }
                    }

                    null -> {
                        Log.i(tag, "updateItem: null")
                    }
                }
            }
            cargandoListaActualizada()
        }
    }

    private fun cargandoListaActualizada() {
        viewModelScope.launch(Dispatchers.IO) {
            _transactionUiState.update { _transactionUiState.value.copy(isUpdateItem = true) }
            val data = dbm.getTransactions()
            _transactionUiState.update {
                _transactionUiState.value.copy(items = data, isUpdateItem = false)
            }
        }
    }

    //..................................funciones que se usan para eliminar un item........................
    //eliminando un ingreso
    private fun updateIngresosTotales(item: TotalIngresosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val totalIngresos = dbm.getTotalIngresos()?.totalIngresos
                val nuevoValor = totalIngresos?.minus(item.totalIngresos!!)
                totalIngresosFirestore.createOrUpdate(TotalIngresosModel(totalIngresos = nuevoValor))
            } catch (e: FirebaseFirestoreException) {
                Log.e(tag, "Error de Firestore en funcion updateIngresosTotales: ${e.message}")
            } catch (e: Exception) {
                Log.e(
                    tag,
                    "Error en updateIngresosTotales: No se pudo obtener los datos de los ingresos ${e.message}"
                )
            }
        }
    }

    //eliminando un gasto
    private fun updateGastos(item: TotalGastosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val totalGastos = dbm.getTotalGastos()?.totalGastos
                val nuevoValor = totalGastos?.minus(item.totalGastos!!)
                totalGastosFirestore.createOrUpdate(TotalGastosModel(totalGastos = nuevoValor))
            } catch (e: FirebaseFirestoreException) {
                Log.e(tag, "Error de Firestore en funcion updateGastos: ${e.message}")
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
        item: CurrentMoneyModel
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (listTransactions.size == 1) {
                // Si es el último elemento, establecer el saldo directamente a 0.0
                currentMoneyFirestore.createOrUpdate(item)
                Log.d(tag, "updateCurrentMoneyList listTransaction.size == 1: $item")
            } else {
                val newValue: Double = dbm.getCurrentMoney()?.money!! + item.money!!
                currentMoneyFirestore.createOrUpdate(item.copy(money = maxOf(newValue, 0.0)))
            }
        }
    }

    private fun updateCurrentMoney(item: CurrentMoneyModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentMoneyFirestore.createOrUpdate(item)
            } catch (e: Exception) {
                Log.e(tag, " Error en updateCurrentMoney: ${e.message}")
            }
        }
    }

    private fun updateTotalIngresos(item: TotalIngresosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                totalIngresosFirestore.createOrUpdate(item)
            } catch (e: Exception) {
                Log.e(tag, " Error en updateTotalIngresos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA ACTUALIZAR EL TOTAL GASTOS AL EDITAR UN GASTO
    private fun updateTotalGastos(item: TotalGastosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                totalGastosFirestore.createOrUpdate(item)
            } catch (e: Exception) {
                Log.e(tag, "Error en updateTotalGastos: ${e.message}")
            }
        }
    }

    //FUNCION QUE SE USA PARA EDITAR UN ITEM DE LA LISTA
    private fun updateItemList(item: TransactionModel) {
        Log.d(tag, "updateItemList: $item")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                transactionsFirestore.update(item)
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

//    fun updateInteractionState(newState: TransactionsUiState) {
//        _interactionState.value = newState
//    }

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
}