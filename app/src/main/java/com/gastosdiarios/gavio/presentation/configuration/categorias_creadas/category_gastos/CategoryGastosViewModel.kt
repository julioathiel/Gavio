package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryGastosViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val repository: UserCategoryGastosFirestore,
) : ViewModel() {

    private val  _uiState = MutableStateFlow(ListUiState<UserCreateCategoryModel>())
    val uiState: StateFlow<ListUiState<UserCreateCategoryModel>> = _uiState.asStateFlow()

    private val _uiStateDefault = MutableStateFlow(CategoryDefaultModel())
    var uiStateDefault: StateFlow<CategoryDefaultModel> = _uiStateDefault.asStateFlow()

    private val _selectedCategoryGastos = MutableStateFlow<CategoryCreate?>(null)
    val selectedCategoryGastos: StateFlow<CategoryCreate?> = _selectedCategoryGastos.asStateFlow()

    init { getAllGastos() }

    fun getAllGastos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data: List<UserCreateCategoryModel> =
                withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
            _uiState.update { it.copy(items = data, isLoading = false) }
        }
    }

    fun createNewCategoryGastos(item: UserCreateCategoryModel) {
        viewModelScope.launch {
            repository.create(item)
            isActivatedTrue()//activa el boton creado para borrar todoo
            limpiandoCampoBottomSheet()
            cargandoListaActualizada()
        }
    }

    fun selectedParaEditar(item: UserCreateCategoryModel, iconSelect: Int) {
        viewModelScope.launch {
            _uiStateDefault.update {
                it.copy(
                    uid = item.uid!!, titleBottomSheet = item.categoryName!!,
                    categoryType = CategoryTypeEnum.GASTOS,
                    selectedCategory = CategoryCreate(name = "", iconSelect),
                    isSelectedEditItem = true, onDismiss = true
                )
            }
        }
    }

    fun actualizandoItem(item: UserCreateCategoryModel) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
            val itemExisting = data.find { it.uid == item.uid }
            if (itemExisting != null) {
                repository.update(item)
            }
            limpiandoCampoBottomSheet()
            cargandoListaActualizada()
        }
    }

    fun eliminarItemSelected(
        item: UserCreateCategoryModel
    ) {
        viewModelScope.launch {
            repository.delete(item) //elimina el item seleccionado
            limpiandoElementoViejo(item)//elimina de la lista predeterminada
            limpiandoCampoBottomSheet()
            cargandoListaActualizada()
        }
    }

    fun borrandoLista() {
        viewModelScope.launch {
            val lista = dbm.getUserCategoryGastos()
            // Elimina todas las categorias de la base de datos
            for (item in lista) {
                repository.delete(item)
                limpiandoElementoViejo(item)
            }
            cargandoListaActualizada()
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

    private fun limpiandoElementoViejo(itemViejo: UserCreateCategoryModel) {
        val categoriaEliminar =
            CategoryGastos(itemViejo.categoryName!!, itemViejo.categoryIcon!!.toInt())
        //REMUEVE EL ITEM ELIMINADO DE LA LISTA DE CATEGORIA PREDETERMINADA
        defaultCategoriesGastosList.removeAll { it == categoriaEliminar }
    }

    private fun cargandoListaActualizada() {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdateItem = true) }
            val data = withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
            _uiState.update { it.copy(items = data, isUpdateItem = false) }
        }
    }

    fun isActivatedTrue() {
        _uiStateDefault.update { it.copy(isActivated = true) }
    }

    fun isActivatedFalse() {
        _uiStateDefault.update { it.copy(isActivated = false) }
    }

    fun onDismissSet(value: Boolean){
        _uiStateDefault.update { it.copy(onDismiss = value) }
    }

    fun actualizarTitulo(title: String) {
        _uiStateDefault.update { it.copy(titleBottomSheet = title) }
    }

    fun selectedIcon(selectedIcon: CategoryCreate) {
        _uiStateDefault.update { it.copy(selectedCategory = selectedIcon) }
    }
}