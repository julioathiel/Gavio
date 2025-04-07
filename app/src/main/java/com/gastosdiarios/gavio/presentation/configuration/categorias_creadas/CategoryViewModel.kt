package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.data.domain.model.CategoryGastos
import com.gastosdiarios.gavio.data.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.data.repository.DataBaseManager
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryGastosFirestore
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import com.gastosdiarios.gavio.data.ui_state.UiStateList
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
        MutableStateFlow<UiStateList<UserCreateCategoryModel>>(UiStateList.Loading)
    val uiStateIngresos = _uiStateIngresos.onStart {
        getAllIngresos()
    }.catch { throwable ->
        _uiStateIngresos.update { UiStateList.Error(throwable = throwable) }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _uiStateGastos =
        MutableStateFlow<UiStateList<UserCreateCategoryModel>>(UiStateList.Loading)
    val uiStateGastos = _uiStateGastos.onStart {
        getAllGastos()
    }
        .catch { throwable ->
            _uiStateGastos.update { UiStateList.Error(throwable = throwable) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), UiStateList.Loading)

    private val _uiStateDefault = MutableStateFlow(CategoryDefaultModel())
    var uiStateDefault: StateFlow<CategoryDefaultModel> = _uiStateDefault.asStateFlow()

    private val _dataList = MutableStateFlow(false)
    val dataList: StateFlow<Boolean> = _dataList.asStateFlow()


    private fun getAllGastos() {
        viewModelScope.launch {
            if (IsInternetAvailableUtils.isInternetAvailable(appContext)) {
                try {
                    withContext(Dispatchers.IO) {
                        dbm.getUserCategoryGastos().collect { db ->
                            if (db.isEmpty()) {
                                _uiStateGastos.update { UiStateList.Empty }
                            } else {
                                _uiStateGastos.update { UiStateList.Success(db) }
                            }
                        }
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
                    withContext(Dispatchers.IO) {
                        dbm.getUserCategoryIngresos().collect { db ->
                            if (db.isEmpty()) {
                                _uiStateIngresos.update { UiStateList.Empty }
                            } else {
                                _uiStateIngresos.update { UiStateList.Success(db) }
                            }
                        }
                    }
                } catch (e: Exception) {
                    _uiStateIngresos.update { UiStateList.Error(throwable = e) }
                }
            } else {
                _uiStateDefault.update { it.copy(errorConectionInternet = true) }
            }
        }
    }

    fun borrandoLista(typeCategory: TipoTransaccion) {
        viewModelScope.launch {
            if (typeCategory == TipoTransaccion.INGRESOS) {
                dbm.getUserCategoryIngresos().collect { db ->
                    // Elimina todas las categorias de la base de datos
                    for (item in db) {
                        repositoryIngresos.delete(item)
                        limpiandoElementoViejo(item)
                    }
                    cargandoListaActualizada(typeCategory)
                }

            } else {
                dbm.getUserCategoryGastos().collect { db ->
                    // Elimina todas las categorias de la base de datos
                    for (item in db) {
                        repositoryGastos.delete(item)
                        limpiandoElementoViejo(item)
                    }
                    cargandoListaActualizada(typeCategory)
                }
            }
        }
    }


    fun selectedParaEditar(
        item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel,
        iconSelect: Int
    ) {
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

    fun eliminarItemSelected(item: UserCreateCategoryModel, typeCategory: TipoTransaccion) {
        viewModelScope.launch(Dispatchers.IO) {
            if (item.categoryType == TipoTransaccion.INGRESOS) {
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
            if (item.categoryType == TipoTransaccion.INGRESOS) {
                withContext(Dispatchers.IO) {
                    dbm.getUserCategoryIngresos().collect { db ->
                        val itemExisting = db.find { it.uid == item.uid }
                        if (itemExisting != null) repositoryIngresos.update(item)
                        cargandoListaActualizada(item.categoryType)
                    }
                }
            } else {
                withContext(Dispatchers.IO) {
                    dbm.getUserCategoryGastos().collect { db ->
                        val itemExisting = db.find { it.uid == item.uid }
                        if (itemExisting != null) repositoryGastos.update(item)
                        cargandoListaActualizada(item.categoryType!!)
                    }
                }
            }
            limpiandoCampoBottomSheet()
        }
    }

    private fun limpiandoElementoViejo(itemViejo: UserCreateCategoryModel) {
        if (itemViejo.categoryType == TipoTransaccion.INGRESOS) {
            val categoriaEliminar =
                CategoryIngresos(
                    itemViejo.categoryName!!,
                    itemViejo.categoryIcon!!.toInt()
                )
            defaultCategoriesIngresosList.removeAll { it == categoriaEliminar }
        } else {
            //REMUEVE EL ITEM ELIMINADO DE LA LISTA DE CATEGORIA PREDETERMINADA
            val categoriaEliminar = CategoryGastos(
                    itemViejo.categoryName!!,
                    itemViejo.categoryIcon!!.toInt()
                )
            defaultCategoriesGastosList.removeAll { it == categoriaEliminar }
        }
    }

    fun createNewCategory(item: UserCreateCategoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (item.categoryType == TipoTransaccion.INGRESOS) {
                repositoryIngresos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(TipoTransaccion.INGRESOS)
            } else {
                repositoryGastos.create(item)
                isActivatedTrue()//activa el boton creado para borrar todoo
                cargandoListaActualizada(item.categoryType!!)
            }
        }
        limpiandoCampoBottomSheet()
    }

    private fun cargandoListaActualizada(typeCategory: TipoTransaccion) {
        viewModelScope.launch {
            _dataList.update { true }
            try {
                val ingresosFlow = dbm.getUserCategoryIngresos()
                val gastosFlow = dbm.getUserCategoryGastos()

                val mergedFlow = when (typeCategory) {
                    TipoTransaccion.INGRESOS -> ingresosFlow
                    TipoTransaccion.GASTOS -> gastosFlow
                }

                mergedFlow.collect { data ->
                    if (data.isEmpty()) {
                        if (typeCategory == TipoTransaccion.INGRESOS) {
                            _uiStateIngresos.update { UiStateList.Empty }
                        } else {
                            _uiStateGastos.update { UiStateList.Empty }
                        }
                    } else {
                        if (typeCategory == TipoTransaccion.INGRESOS) {
                            _uiStateIngresos.update { UiStateList.Success(data) }
                        } else {
                            _uiStateGastos.update { UiStateList.Success(data) }
                        }
                    }
                }
                _dataList.update { false }
            } catch (e: Exception) {
                if (typeCategory == TipoTransaccion.INGRESOS) {
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