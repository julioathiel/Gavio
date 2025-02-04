package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.CategoryCreate
import com.gastosdiarios.gavio.data.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.data.domain.model.CategoryGastos
import com.gastosdiarios.gavio.data.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.gastosdiarios.gavio.utils.IsInternetAvailableUtils
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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val dbm: DataBaseManager,
    private val repositoryGastos: UserCategoryGastosFirestore,
    private val repositoryIngresos: UserCategoryIngresosFirestore,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiStateIngresos =
        MutableStateFlow<UiStateList<com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel>>(UiStateList.Loading)
    val uiStateIngresos = _uiStateIngresos.onStart {
        getAllIngresos()
    }.catch { throwable ->
        _uiStateIngresos.update { UiStateList.Error(throwable = throwable) }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _uiStateGastos =
        MutableStateFlow<UiStateList<com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel>>(UiStateList.Loading)
    val uiStateGastos = _uiStateGastos.onStart {
        getAllGastos()
    }
        .catch { throwable ->
            _uiStateGastos.update { UiStateList.Error(throwable = throwable) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _uiStateDefault = MutableStateFlow(com.gastosdiarios.gavio.data.domain.model.CategoryDefaultModel())
    var uiStateDefault: StateFlow<com.gastosdiarios.gavio.data.domain.model.CategoryDefaultModel> = _uiStateDefault.asStateFlow()

    private val _dataList = MutableStateFlow(false)
    val dataList: StateFlow<Boolean> = _dataList.asStateFlow()


    private fun getAllGastos() {
        viewModelScope.launch {
            if (IsInternetAvailableUtils.isInternetAvailable(appContext)) {
                try {
                    val data: List<com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel> =
                        withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
                    if (data.isEmpty()) {
                        _uiStateGastos.update { UiStateList.Empty }
                    } else {
                        _uiStateGastos.update { UiStateList.Success(data) }
                    }
                } catch (e: Exception) {
                    _uiStateGastos.update { UiStateList.Error(throwable = e) }
                }
            } else {
                _uiStateDefault.update { it.copy(errorConectionInternet = true) }
            }
        }
    }

    private fun getAllIngresos() {
        viewModelScope.launch {
            if (IsInternetAvailableUtils.isInternetAvailable(appContext)) {
                try {
                    val data: List<com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel> =
                        withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
                    if (data.isEmpty()) {
                        _uiStateIngresos.update { UiStateList.Empty }
                    } else {
                        _uiStateIngresos.update { UiStateList.Success(data) }
                    }
                } catch (e: Exception) {
                    _uiStateIngresos.update { UiStateList.Error(throwable = e) }
                }
            } else {
                _uiStateDefault.update { it.copy(errorConectionInternet = true) }
            }
        }
    }

    fun borrandoLista(typeCategory: com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion) {
        viewModelScope.launch {
            if (typeCategory == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                val lista = dbm.getUserCategoryIngresos()
                // Elimina todas las categorias de la base de datos
                for (item in lista) {
                    repositoryIngresos.delete(item)
                    limpiandoElementoViejo(item)
                }
                cargandoListaActualizada(typeCategory)
            } else {
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


    fun selectedParaEditar(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel, iconSelect: Int) {
        viewModelScope.launch {
            if (item.categoryType == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                _uiStateDefault.update {
                    it.copy(
                        uid = item.uid!!,
                        titleBottomSheet = item.categoryName!!,
                        categoryType = com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS,
                        selectedCategory = com.gastosdiarios.gavio.data.domain.model.CategoryCreate(
                            name = "",
                            iconSelect
                        ),
                        isSelectedEditItem = true, onDismiss = true
                    )
                }
            } else {
                _uiStateDefault.update {
                    it.copy(
                        onDismiss = true,
                        uid = item.uid!!,
                        titleBottomSheet = item.categoryName!!,
                        categoryType = com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.GASTOS,
                        selectedCategory = com.gastosdiarios.gavio.data.domain.model.CategoryCreate(
                            name = "",
                            iconSelect
                        ),
                        isSelectedEditItem = true,
                    )
                }
            }
        }
    }

    fun eliminarItemSelected(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel, typeCategory: com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion) {
        viewModelScope.launch {
            if (item.categoryType == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
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

    fun actualizandoItem(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel) {
        viewModelScope.launch {
            if (item.categoryType == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
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

    private fun limpiandoElementoViejo(itemViejo: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel) {
        if (itemViejo.categoryType == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
            val categoriaEliminar =
                com.gastosdiarios.gavio.data.domain.model.CategoryIngresos(
                    itemViejo.categoryName!!,
                    itemViejo.categoryIcon!!.toInt()
                )
            com.gastosdiarios.gavio.data.domain.model.defaultCategoriesIngresosList.removeAll { it == categoriaEliminar }
        } else {
            //REMUEVE EL ITEM ELIMINADO DE LA LISTA DE CATEGORIA PREDETERMINADA
            val categoriaEliminar =
                com.gastosdiarios.gavio.data.domain.model.CategoryGastos(
                    itemViejo.categoryName!!,
                    itemViejo.categoryIcon!!.toInt()
                )
            com.gastosdiarios.gavio.data.domain.model.defaultCategoriesGastosList.removeAll { it == categoriaEliminar }
        }
    }

    fun createNewCategory(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel) {
        viewModelScope.launch {
            if (item.categoryType == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                repositoryIngresos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS)
            } else {
                repositoryGastos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(item.categoryType!!)
            }
        }
        limpiandoCampoBottomSheet()
    }

    private fun cargandoListaActualizada(typeCategory: com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion) {
        viewModelScope.launch {
            _dataList.update { true }
            try {
                val data = when (typeCategory) {
                    com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS -> withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
                    com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.GASTOS -> withContext(Dispatchers.IO) { dbm.getUserCategoryGastos() }
                }


                if (data.isEmpty()) {
                    if (typeCategory == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                        _uiStateIngresos.update { UiStateList.Empty }
                    } else {
                        _uiStateGastos.update { UiStateList.Empty }
                    }
                } else {
                    if (typeCategory == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                        _uiStateIngresos.update { UiStateList.Success(data) }
                    } else {
                        _uiStateGastos.update { UiStateList.Success(data) }
                    }
                }
                _dataList.update { false }
            } catch (e: Exception) {
                if (typeCategory == com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion.INGRESOS) {
                    _uiStateIngresos.update { UiStateList.Error(throwable = e) }
                }
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

    fun selectedIcon(selectedIcon: com.gastosdiarios.gavio.data.domain.model.CategoryCreate) {
        _uiStateDefault.update { it.copy(selectedCategory = selectedIcon) }
    }
}