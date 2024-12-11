package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.CategoryIngresos
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.domain.repository.DataBaseManager
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.UserCategoryIngresosFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CategoryIngresosViewModel @Inject constructor(
    private val repository: UserCategoryIngresosFirestore,
    private val dbm: DataBaseManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<UserCreateCategoryModel>>(emptyList())
    var uiState: StateFlow<List<UserCreateCategoryModel>> = _uiState

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _isLoadingData = mutableStateOf(false)
    val isLoadingData: State<Boolean> = _isLoadingData

    private val _isActivated = mutableStateOf(false)
    val isActivated: State<Boolean> = _isActivated

    private val _idDelElemento = MutableLiveData<Int>()
    val idDelElemento: LiveData<Int> = _idDelElemento

    private val _tituloBottomSheet = MutableStateFlow("")
    val tituloBottomSheet: StateFlow<String> = _tituloBottomSheet.asStateFlow()

    private val _selectedCategoryIngresos = MutableStateFlow<CategoryCreate?>(null)
    val selectedCategoryIngresos: StateFlow<CategoryCreate?> = _selectedCategoryIngresos.asStateFlow()

    private val _isEditarSeleccion = mutableStateOf(false)
    val isEditarSeleccion: State<Boolean> = _isEditarSeleccion

    private val _onDismiss = mutableStateOf(false)
    val onDismiss: State<Boolean> = _onDismiss

    init {
        getAllIngresos()
    }

    fun getAllIngresos() {
        viewModelScope.launch {
            val data: List<UserCreateCategoryModel> = dbm.getUserCategoryIngresos()
            _uiState.value = data
            _isLoading.value = false
        }
    }

    fun isActivatedTrue() { _isActivated.value = true }
    fun isActivatedFalse() { _isActivated.value = false }
    fun onDismissSet(value: Boolean) { _onDismiss.value = value }
    fun iconoSelecionadoIngresos(iconoSeleccionado: CategoryCreate) { _selectedCategoryIngresos.value = iconoSeleccionado }
    fun actualizarTituloIngresos(it: String) { _tituloBottomSheet.value = it }

    fun createNewCategoryIngresos(item: UserCreateCategoryModel) {
        viewModelScope.launch {
            repository.create(
                UserCreateCategoryModel(
                    categoryName = item.categoryName,
                    categoryIcon = item.categoryIcon,
                    categoryType = CategoryTypeEnum.INGRESOS
                )
            )
            isActivatedTrue()
            deleteCampoBottomSheetIngresos()
            cargandoListaActualizada()
        }
    }


    fun editItemSelected(item: UserCreateCategoryModel, iconoSeleccionado: Int) {
        viewModelScope.launch {
            _idDelElemento.value = item.uid!!.toInt() // para compartirlo con la otra funcion
            _tituloBottomSheet.value = item.categoryName!!
            _selectedCategoryIngresos.value = CategoryCreate(name = "", iconoSeleccionado)
            _isEditarSeleccion.value = true //activa para actualizar al momento de guardar
            _onDismiss.value = true //abre el bottomSheet
        }
    }

    fun deleteItemSelected(
        item: UserCreateCategoryModel
    ) {
        viewModelScope.launch {
            //elimina  un elemento de la base de datos
            repository.delete(item)

            val categoriaEliminar = CategoryIngresos(
                item.categoryName!!,
                item.categoryIcon!!.toInt()
            )

            defaultCategoriesIngresosList.removeAll { it == categoriaEliminar }
            cargandoListaActualizada()
        }
    }

    fun deleteAllList() {
        viewModelScope.launch {
            val lista: List<UserCreateCategoryModel> = dbm.getUserCategoryIngresos()
            // Elimina todas las categorias de la base de datos
            for (item in lista) {
                repository.delete(item)
                limpiandoElementoViejo(item)
            }
            cargandoListaActualizada()
        }
    }

    fun updateItemIngresos(itemModel: UserCreateCategoryModel) {
        viewModelScope.launch {
            // Actualiza un elemento en la base de datos
            deleteCampoBottomSheetIngresos()
            repository.update(itemModel) //Actualiza en la base de datos
            cargandoListaActualizada()
        }
    }
    private fun cargandoListaActualizada() {
        viewModelScope.launch {
            _isLoadingData.value = true
            val data = withContext(Dispatchers.IO) { dbm.getUserCategoryIngresos() }
            _uiState.value = data
            _isLoadingData.value = false
        }
    }

    private fun deleteCampoBottomSheetIngresos() {
        _tituloBottomSheet.value = "" //limpia textField
        _selectedCategoryIngresos.value = null //limpia seleccion
        _isEditarSeleccion.value = false //desactiva para no editar
    }

    private fun limpiandoElementoViejo(itemViejo: UserCreateCategoryModel) {
        val categoriaEliminar =
            CategoryIngresos(itemViejo.categoryName!!, itemViejo.categoryIcon!!.toInt())
        //REMUEVE EL ITEM ELIMINADO DE LA LISTA DE CATEGORIA PREDETERMINADA
        defaultCategoriesIngresosList.removeAll { it == categoriaEliminar }
    }
}