package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.domain.model.DataList
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
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

    private val _uiState =
        MutableStateFlow<UiStateList<GastosProgramadosModel>>(UiStateList.Loading)
    val uiState = _uiState.onStart {
        getAllGastosProgramados()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L), UiStateList.Loading
    )

    private val _dataList = MutableStateFlow(DataList<GastosProgramadosModel>())
    val dataList: StateFlow<DataList<GastosProgramadosModel>> = _dataList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private fun getAllGastosProgramados() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data: List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
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
        _dataList.update { it.copy(selectedItems = emptyList(), selectionMode = false) }
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
        _dataList.update { it.copy(updateItem = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = gastosProgramadosFirestore.get()

                if (data.isEmpty()) {
                    _uiState.update { UiStateList.Empty }
                } else {
                    _dataList.update { it.copy(updateItem = false) }
                    _uiState.update { UiStateList.Success(data) }
                }
            }catch (e:Exception){
                Toast.makeText(context, "Error al actualizar la lista", Toast.LENGTH_SHORT).show()
                Log.e("Error", e.message.toString())
            }
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

    // funcion que se usa cuando se edita y se guarda el item
    fun clearSelection(item: GastosProgramadosModel) {
        isCreateFalse()
        //hacce que se deseleccione ya que se edito y se guardo
        _dataList.update { it.copy(selectionMode = false, selectedItems = emptyList()) }
        onClick(item)
    }

    fun retryLoadData() {
        _uiState.update { UiStateList.Loading }
        getAllGastosProgramados()
    }

    fun refreshData() {
      viewModelScope.launch(Dispatchers.IO) {
          try {
              _isRefreshing.update { it.copy(isRefreshing = true) }
              val data: List<GastosProgramadosModel> = gastosProgramadosFirestore.get()
              if (data.isEmpty()) {
                  _uiState.update { UiStateList.Empty }
              } else {
                  _isRefreshing.update { it.copy(isRefreshing = false) }
                  _uiState.update { UiStateList.Success(data) }
              }
          }catch (e:Exception){
              _uiState.update { UiStateList.Error(e.message ?: "Error desconocido", e) }
          }
      }
    }
}