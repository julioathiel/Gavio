package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.BotonGastosIngresosPantallaGastos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ContentBottomSheet
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ToolbarGastos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ItemCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    categoryType: CategoryTypeEnum,
    onBack: () -> Unit
) {
    // ... (Lógica común para ambas pantallas) ...
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
 //   BackHandler { onBack() }
    var categoryTypes by remember { mutableStateOf(categoryType) }
    val uiStateDefault by viewModel.uiStateDefault.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.getAllGastos()
        viewModel.getAllIngresos()
    }

    PantallaDeCategoriasCreadas(
        uiStateDefault = uiStateDefault,
        viewModel = viewModel,
        sheetState = sheetState,
        categoryTypes = categoryTypes,
        onBack = { onBack() },
        content = {
            BotonGastosIngresosPantallaGastos(
                Modifier
                    .fillMaxWidth()
                    .height(51.dp)
                    .padding(horizontal = 16.dp)
            ) { tipoClase ->
                categoryTypes =
                    if (tipoClase == CategoryTypeEnum.INGRESOS) CategoryTypeEnum.INGRESOS else CategoryTypeEnum.GASTOS
            }
            when (categoryTypes) {
                CategoryTypeEnum.INGRESOS -> {
                    val ingresosActions = object : CategoryActions {
                        override fun onEditClick(
                            item: UserCreateCategoryModel,
                            iconSelect: Int
                        ) {
                            viewModel.selectedParaEditar(item, iconSelect)
                        }

                        override fun onDeleteClick(item: UserCreateCategoryModel) {
                            viewModel.eliminarItemSelected(item, typeCategory = categoryTypes)
                        }
                    }
                    // Contenido para la pantalla de ingresos
                    StateContentCategoryIngresos(
                        viewModel = viewModel,
                        ingresosActions = ingresosActions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                    )
                }

                CategoryTypeEnum.GASTOS -> {
                    val gastosActions = object : CategoryActions {
                        override fun onEditClick(
                            item: UserCreateCategoryModel,
                            iconSelect: Int
                        ) {
                            viewModel.selectedParaEditar(item, iconSelect)
                        }

                        override fun onDeleteClick(item: UserCreateCategoryModel) {
                            viewModel.eliminarItemSelected(item, typeCategory = categoryTypes)
                        }
                    }
                    // Contenido para la pantalla de gastos
                    StateContentCategoryGastos(
                        viewModel = viewModel,
                        gastosActions = gastosActions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(it)
                    )
                }
            }
        }
    )
}

@Composable
fun StateContentCategoryIngresos(
    viewModel: CategoryViewModel,
    ingresosActions: CategoryActions,
    modifier: Modifier
) {
    val uiStateIngresos by viewModel.uiStateIngresos.collectAsState()
    Log.d("StateContentCategoryIngresos", "uiStateIngresos: $uiStateIngresos")
    when {
        uiStateIngresos.isLoading -> CommonsLoadingScreen(modifier.fillMaxSize())
        uiStateIngresos.items.isEmpty() -> {
            // Si la lista está vacía, mostrar
            viewModel.isActivatedFalse()
            ContentCategoryEmpty(viewModel, modifier)
        }

        uiStateIngresos.isUpdateItem -> {
            CommonsLoadingData()
            ListContentTypeCategory(
                uiStateIngresos.items,
                viewModel,
                ingresosActions,
                modifier
            )
            CommonsLoadingData()
        }

        else -> {
            ListContentTypeCategory(
                uiState = uiStateIngresos.items,
                viewModel = viewModel,
                ingresosActions,
                modifier = modifier
            )
        }
    }
}


@Composable
fun StateContentCategoryGastos(
    viewModel: CategoryViewModel,
    gastosActions: CategoryActions,
    modifier: Modifier
) {
    //Contenido para la pantalla de gastos
    val uiStateGastos by viewModel.uiStateGastos.collectAsState()

    when {
        uiStateGastos.isLoading -> CommonsLoadingScreen(Modifier.fillMaxSize())
        uiStateGastos.items.isEmpty() -> {
            // Si la lista está vacía, mostrar
            viewModel.isActivatedFalse()
            ContentCategoryEmpty(viewModel, modifier)
        }

        uiStateGastos.isUpdateItem -> {
            CommonsLoadingData()
            ListContentTypeCategory(
                uiStateGastos.items,
                viewModel,
                gastosActions,
                modifier
            )
            CommonsLoadingData()
        }

        else -> {
            ListContentTypeCategory(
                uiStateGastos.items,
                viewModel,
                gastosActions,
                modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDeCategoriasCreadas(
    uiStateDefault: CategoryDefaultModel,
    viewModel: CategoryViewModel,
    sheetState: SheetState,
    categoryTypes: CategoryTypeEnum,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit = {},
) {
    BottomSheetScaffold(
        topBar = {
            ToolbarGastos(
                uiStateDefault,
                viewModel,
                onBack = { onBack() },
                onClickAction = {
                    viewModel.borrandoLista(categoryTypes)
                }
            )
        },
        sheetMaxWidth = 0.dp,
        sheetContent = {
            if (uiStateDefault.onDismiss) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onDismissSet(false) },
                    sheetState = sheetState,
                    content = {
                        ContentBottomSheet(
                            onDismiss = { viewModel.onDismissSet(false) },
                            categoryTypes,
                            uiStateDefault,
                            viewModel
                        )
                    },
                )
            }
        },
        content = { paddingValues ->
            content(paddingValues)
        }
    )
}

@Composable
fun ListContentTypeCategory(
    uiState: List<UserCreateCategoryModel>,
    viewModel: CategoryViewModel,
    categoryActions: CategoryActions,
    modifier: Modifier,
) {
    viewModel.isActivatedTrue()
    Box(
        modifier
            .fillMaxSize()
            .padding(top = 56.dp)
    ) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(uiState.size) { nuevoItem ->
                val item = uiState[nuevoItem]
                ItemCategory(item = item, categoryActions = categoryActions)
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onDismissSet(true) },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(20.dp)
                .zIndex(1f)
        )
        { Icon(Icons.Default.Add, contentDescription = null) }
    }
}

@Composable
fun ContentCategoryEmpty(viewModel: CategoryViewModel, modifier: Modifier) {
    CommonsEmptyFloating(
        onClick = {
            viewModel.onDismissSet(true)
        }, modifier = modifier.padding(top = 56.dp)
    )
}