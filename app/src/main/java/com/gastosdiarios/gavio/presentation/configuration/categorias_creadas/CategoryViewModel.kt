package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.gastosdiarios.gavio.utils.IsInternetAvailableUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val repositoryGastos: UserCategoryGastosFirestore,
    private val repositoryIngresos: UserCategoryIngresosFirestore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiStateDefault = MutableStateFlow(CategoryDefaultModel())
    var uiStateDefault: StateFlow<CategoryDefaultModel> = _uiStateDefault.asStateFlow()

    private val _uiStateGastos = MutableStateFlow(ListUiState<UserCreateCategoryModel>())
    val uiStateGastos: StateFlow<ListUiState<UserCreateCategoryModel>> =
        _uiStateGastos.asStateFlow()

    private val _uiStateIngresos = MutableStateFlow(ListUiState<UserCreateCategoryModel>())
    val uiStateIngresos: StateFlow<ListUiState<UserCreateCategoryModel>> =
        _uiStateIngresos.asStateFlow()

    init {
        getAllGastos()
        getAllIngresos()
    }

    fun getAllGastos() {
        viewModelScope.launch {
            if(IsInternetAvailableUtils.isInternetAvailable(appContext)){
                _uiStateGastos.update { it.copy(isLoading = true) }
                val data: List<UserCreateCategoryModel> =
                    withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
                _uiStateGastos.update { it.copy(items = data, isLoading = false) }
            }else{
                _uiStateDefault.update { it.copy(errorConectionInternet = true) }
            }
        }
    }

    fun getAllIngresos() {
        viewModelScope.launch {
            _uiStateIngresos.update { it.copy(isLoading = true) }
            val data: List<UserCreateCategoryModel> =
                withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
            _uiStateIngresos.update { it.copy(items = data, isLoading = false) }
        }
    }

    fun borrandoLista(typeCategory: CategoryTypeEnum) {
        viewModelScope.launch {
            if (typeCategory == CategoryTypeEnum.INGRESOS) {
                val lista = dbm.getUserCategoryIngresos()
                // Elimina todas las categorias de la base de datos
                for (item in lista) {
                    repositoryIngresos.delete(item)
                    limpiandoElementoViejo(item)
                }
                cargandoListaActualizada(typeCategory)
            }else{
                val lista = dbm.getUserCategoryGastos()
                // Elimina todas las categorias de la base de datos
                for (item in lista) {
                    repositoryGastos.delete(item)
                    limpiandoElementoViejo(item)
                }
                cargandoListaActualizada(typeCategory)
            }
        }
    }


    fun selectedParaEditar(item: UserCreateCategoryModel, iconSelect: Int) {
        viewModelScope.launch {
            if (item.categoryType == CategoryTypeEnum.INGRESOS) {
                _uiStateDefault.update {
                    it.copy(
                        uid = item.uid!!,
                        titleBottomSheet = item.categoryName!!,
                        categoryType = CategoryTypeEnum.INGRESOS,
                        selectedCategory = CategoryCreate(name = "", iconSelect),
                        isSelectedEditItem = true, onDismiss = true
                    )
                }
            }else{
                _uiStateDefault.update {
                    it.copy(
                        onDismiss = true,
                        uid = item.uid!!,
                        titleBottomSheet = item.categoryName!!,
                        categoryType = CategoryTypeEnum.GASTOS,
                        selectedCategory = CategoryCreate(name = "", iconSelect),
                        isSelectedEditItem = true,
                    )
                }
            }
        }
    }

    fun eliminarItemSelected(item: UserCreateCategoryModel, typeCategory: CategoryTypeEnum) {
        viewModelScope.launch {
            if (item.categoryType == CategoryTypeEnum.INGRESOS) {
                repositoryIngresos.delete(item)
                limpiandoElementoViejo(item)
                cargandoListaActualizada(typeCategory)
            } else {
                repositoryGastos.delete(item) //elimina el item seleccionado
                limpiandoElementoViejo(item)//elimina de la lista predeterminada
                cargandoListaActualizada(typeCategory)
            }
            limpiandoCampoBottomSheet()
        }
    }

    fun actualizandoItem(item: UserCreateCategoryModel) {
        viewModelScope.launch {
            if (item.categoryType == CategoryTypeEnum.INGRESOS) {
                val data = withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
                val itemExisting = data.find { it.uid == item.uid }
                if (itemExisting != null) repositoryIngresos.update(item)
                cargandoListaActualizada(item.categoryType)
            } else {
                val data = withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
                val itemExisting = data.find { it.uid == item.uid }
                if (itemExisting != null) repositoryGastos.update(item)
                cargandoListaActualizada(item.categoryType!!)
            }
            limpiandoCampoBottomSheet()
        }
    }

    private fun limpiandoElementoViejo(itemViejo: UserCreateCategoryModel) {
        if (itemViejo.categoryType == CategoryTypeEnum.INGRESOS) {
            val categoriaEliminar =
                CategoryIngresos(itemViejo.categoryName!!, itemViejo.categoryIcon!!.toInt())
            defaultCategoriesIngresosList.removeAll { it == categoriaEliminar }
        } else {
            //REMUEVE EL ITEM ELIMINADO DE LA LISTA DE CATEGORIA PREDETERMINADA
            val categoriaEliminar =
                CategoryGastos(itemViejo.categoryName!!, itemViejo.categoryIcon!!.toInt())
            defaultCategoriesGastosList.removeAll { it == categoriaEliminar }
        }
    }

    fun createNewCategory(item: UserCreateCategoryModel) {
        viewModelScope.launch {
            if (item.categoryType == CategoryTypeEnum.INGRESOS) {
                repositoryIngresos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(CategoryTypeEnum.INGRESOS)
            } else {
                repositoryGastos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(item.categoryType!!)
            }
        }
        limpiandoCampoBottomSheet()
    }

    private fun cargandoListaActualizada(typeCategory: CategoryTypeEnum) {
        viewModelScope.launch {
            if (typeCategory == CategoryTypeEnum.INGRESOS) {
                _uiStateIngresos.update { it.copy(isUpdateItem = true) }
                val data = withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
                _uiStateIngresos.update { it.copy(items = data, isUpdateItem = false) }
            } else {
                _uiStateGastos.update { it.copy(isUpdateItem = true) }
                val data = withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
                _uiStateGastos.update { it.copy(items = data, isUpdateItem = false) }
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

    fun isActivatedTrue() {
        _uiStateDefault.update { it.copy(isActivated = true) }
    }
    fun isActivatedFalse() {
        _uiStateDefault.update { it.copy(isActivated = false) }
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
}