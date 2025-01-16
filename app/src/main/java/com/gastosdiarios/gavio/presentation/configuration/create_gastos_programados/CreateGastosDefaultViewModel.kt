package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.RefreshDataModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.CreateGastosProgramadosFireStore
import com.gastosdiarios.gavio.utils.DateUtils.obtenerFechaActual
import com.gastosdiarios.gavio.utils.RefreshDataUtils
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
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class CreateGastosDefaultViewModel @Inject constructor(
    private val repository: CreateGastosProgramadosFireStore,
) : ViewModel() {
    private val _uiStateDefault = MutableStateFlow(CategoryDefaultModel())
    var uiStateDefault: StateFlow<CategoryDefaultModel> = _uiStateDefault.asStateFlow()

    private val _selectedIndices = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIndices: StateFlow<Set<Int>> = _selectedIndices.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

    private val _gastosprogramadosUiState = MutableStateFlow(ListUiState<GastosProgramadosModel>())
    val gastosprogramadosUiStateUiState: StateFlow<ListUiState<GastosProgramadosModel>> =
        _gastosprogramadosUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(RefreshDataModel(isRefreshing = false))
    val isRefreshing: StateFlow<RefreshDataModel> = _isRefreshing.asStateFlow()

    private val _currentMode = MutableStateFlow<Modo?>(null)
    val currentMode: StateFlow<Modo?> = _currentMode.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading
        .onStart {
            getAllGastosprogramados()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

    private fun getAllGastosprogramados() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            val data: List<GastosProgramadosModel> = repository.get()
            _gastosprogramadosUiState.update {
                it.copy(items = data)
            }
            _isLoading.update { false }
        }
    }

    private fun cargandoListaActualizada(typeCategory: CategoryTypeEnum) {
        viewModelScope.launch {
            if (typeCategory == CategoryTypeEnum.INGRESOS) {
//                _uiStateIngresos.update { it.copy(isUpdateItem = true) }
//                val data = withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
//                _uiStateIngresos.update { it.copy(items = data, isUpdateItem = false) }
            } else {
                _gastosprogramadosUiState.update { it.copy(isUpdateItem = true) }
                isActivatedFalse()
                val data = withContext(Dispatchers.IO) { repository.get() }
                _gastosprogramadosUiState.update { it.copy(items = data, isUpdateItem = false) }
            }
        }
    }

    private fun limpiandoCampoBottomSheet() {
        _uiStateDefault.update {
            it.copy(
                titleBottomSheet = "",
                selectedCategory = null,
                isSelectedEditItem = false
            )
        }
    }

    fun refreshData(context: Context) {
        RefreshDataUtils.refreshData(
            viewModelScope,
            isRefreshing = _isRefreshing,
            dataLoading = {
                getAllGastosprogramados()
                Toast.makeText(context, "actualizado", Toast.LENGTH_SHORT).show()
            }
        )
    }


    fun isEditItemTrue() {
        _uiStateDefault.update { it.copy(isSelectedEditItem = true) }
    }

    fun isEditItemFalse() {
        _uiStateDefault.update { it.copy(isSelectedEditItem = false) }

    }

    fun isActivatedTrue() {
        _uiStateDefault.update { it.copy(isActivated = true) }
    }

    fun isActivatedFalse() {
        _uiStateDefault.update { it.copy(isActivated = false) }
        _isSelectionMode.value = false
        _selectedIndices.value = emptySet()
    }

    fun toggleSelection(index: Int) {
        _selectedIndices.value = if (index in selectedIndices.value) {
            /*
              Si el elemento ya está seleccionado, se elimina su índice
               del conjunto selectedIndices. Esto significa que el elemento se deselecciona.
             */
            selectedIndices.value - index
        } else {
            /*
            Si el elemento no está seleccionado, se agrega su índice al
              conjunto selectedIndices. Esto significa que el elemento se selecciona.
              */
            selectedIndices.value + index
        }
        _isSelectionMode.value = selectedIndices.value.isNotEmpty()
    }

    fun onDismissSet(value: Boolean) {
        _uiStateDefault.update { it.copy(onDismiss = value) }
    }

    fun actualizarTitulo(title: String) {
        _uiStateDefault.update { it.copy(titleBottomSheet = title) }
    }

    fun selectedIcon(selectedIcon: CategoryCreate) {
        _uiStateDefault.update { it.copy(selectedCategory = selectedIcon) }
    }


    fun createNewCategory(item: GastosProgramadosModel) {
        viewModelScope.launch {
            if (item.categoryType == CategoryTypeEnum.INGRESOS) {
//                repositoryIngresos.create(item)
//                isActivatedTrue()//activa el boton creado para borrar todoo
//                cargandoListaActualizada(CategoryTypeEnum.INGRESOS)
            } else {
                repository.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(item.categoryType!!)
            }
        }
        limpiandoCampoBottomSheet()
    }

    fun updateItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            repository.update(item)
            isActivatedFalse()
            cargandoListaActualizada(item.categoryType!!)
        }
    }

    fun deleteItem(item: GastosProgramadosModel) {
        viewModelScope.launch {
            repository.delete(item)
            isActivatedFalse()
            cargandoListaActualizada(item.categoryType!!)
        }
    }

    fun setCurrentMode(mode: Modo?) {
        _currentMode.value = mode
    }
}