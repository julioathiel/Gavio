package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.DataList
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.GastosProgramadosFirestore
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
import javax.inject.Inject

@HiltViewModel
class CreateGastosProgramadosViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gastosProgramadosFirestore: GastosProgramadosFirestore
) : ViewModel() {

    private val _gastosProgramadosUiState = MutableStateFlow(ListUiState<GastosProgramadosModel>())
    val gastosProgramadosUiState: StateFlow<ListUiState<GastosProgramadosModel>> =
        _gastosProgramadosUiState.asStateFlow()


    private val _dataList = MutableStateFlow(DataList<GastosProgramadosModel>())
    val dataList: StateFlow<DataList<GastosProgramadosModel>> = _dataList.asStateFlow()


    private val _loading = MutableStateFlow(false)
    val loading = _loading.onStart {
        getAllGastosProgramados()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), false
    )

    private fun getAllGastosProgramados() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.update { true }
            val data: List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
            _gastosProgramadosUiState.update {
                it.copy(items = data, isLoading = false)
            }
            _loading.update { false }
        }
    }


    fun create(item: GastosProgramadosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            gastosProgramadosFirestore.create(item)
            cargandoListaActualizada()
        }
    }

    fun update(item: GastosProgramadosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            gastosProgramadosFirestore.update(item)
            cargandoListaActualizada()
        }
    }

    fun delete(item: GastosProgramadosModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                gastosProgramadosFirestore.delete(item)
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                Log.e("Error", e.message.toString())
            }
        }
    }

    fun deleteItemSelected(item: GastosProgramadosModel) {
        delete(item)
        _dataList.update{ it.copy(selectedItems = emptyList(), selectionMode = false)}
        cargandoListaActualizada()
    }


    fun onClick(item: GastosProgramadosModel) {
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

    fun onLongClick(item: GastosProgramadosModel) {
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

    private fun cargandoListaActualizada() {
        viewModelScope.launch(Dispatchers.IO) {
              _gastosProgramadosUiState.update { it.copy(update = true) }
            val data = gastosProgramadosFirestore.get()
            _gastosProgramadosUiState.update {
                it.copy(items = data, update = false)
            }
        }
    }

    fun isCreateTrue() { _dataList.update { it.copy(isCreate = true) } }
    fun isCreateFalse() { _dataList.update { it.copy(isCreate = false) }  }
    fun isDeleteTrue() { _dataList.update { it.copy(isDelete = true) } }
    fun isDeleteFalse() {_dataList.update { it.copy(isDelete = false) } }

    // funcion que se usa cuando se edita y se guarda el item
    fun clearSelection(item: GastosProgramadosModel) {
        isCreateFalse()
        //hacce que se deseleccione ya que se edito y se guardo
        _dataList.update { it.copy(selectionMode = false, selectedItems = emptyList()) }
        onClick(item)
    }
}