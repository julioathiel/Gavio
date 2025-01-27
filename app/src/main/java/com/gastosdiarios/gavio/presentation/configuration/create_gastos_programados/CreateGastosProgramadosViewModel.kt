package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
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


    private val _isCreate = MutableStateFlow(false)
    val isCreate: StateFlow<Boolean> = _isCreate.asStateFlow()

    private val _isDelete = MutableStateFlow(false)
    val isDelete: StateFlow<Boolean> = _isDelete.asStateFlow()

    private val _selectionMode = MutableStateFlow(false)
    val selectionMode: StateFlow<Boolean> = _selectionMode

    private val _selectedItems = MutableStateFlow<List<GastosProgramadosModel>>(emptyList())
    val selectedItems: StateFlow<List<GastosProgramadosModel>> = _selectedItems

    private val _expandedItem = MutableStateFlow<GastosProgramadosModel?>(null)
    val expandedItem: StateFlow<GastosProgramadosModel?> = _expandedItem


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.onStart {
        getAllGastosProgramados()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), false
    )

    private fun getAllGastosProgramados() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            val data: List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
            _gastosProgramadosUiState.update {
                it.copy(items = data, isLoading = false)
            }
            _isLoading.value = false
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
        _selectedItems.value = emptyList()
        _selectionMode.value = false
        cargandoListaActualizada()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            gastosProgramadosFirestore.deleteAll()
        }
    }


    fun onClickGastosProgramados(item: GastosProgramadosModel) {
        if (_selectionMode.value) {
            _selectedItems.update { currentList ->
                val newList = currentList.toMutableList()
                if (newList.any { it.uid == item.uid }) {
                    newList.removeAll { it.uid == item.uid }
                } else {
                    newList.add(item)
                }
                newList.toList() // Convertir de nuevo a List<GastosProgramadosModel>
            }
            if (_selectedItems.value.isEmpty()) {
                _selectionMode.value = false
            }
        } else {
            _expandedItem.value = if (_expandedItem.value == item) null else item
        }
    }


    fun onLongClickGastosProgramados(item: GastosProgramadosModel) {
         _selectionMode.update { true }
        _selectedItems.update { currentList ->
            val newList = currentList.toMutableList()
            if (newList.any { it.uid == item.uid }) {
                newList.removeAll { it.uid == item.uid }
            } else {
                newList.add(item)
            }
            newList.toList() // Convertir de nuevo a List<GastosProgramadosModel>
        }

    }

    private fun cargandoListaActualizada() {
        viewModelScope.launch(Dispatchers.IO) {
              _gastosProgramadosUiState.update { it.copy(isUpdateItem = true) }
            val data = gastosProgramadosFirestore.get()
            _gastosProgramadosUiState.update {
                it.copy(items = data, isUpdateItem = false)
            }
        }
    }

    fun isCreateTrue() { _isCreate.value = true }
    fun isCreateFalse() { _isCreate.value = false }
    fun isDeleteTrue() { _isDelete.value = true }
    fun isDeleteFalse() { _isDelete.value = false }

    // funcion que se usa cuando se edita y se guarda el item
    fun clearSelection(item: GastosProgramadosModel) {
        isCreateFalse()
        onClickGastosProgramados(item)
    }
}