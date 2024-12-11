package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.navigation.Routes
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.components.BotonGastosIngresosPantallaGastos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.components.ContentBottomSheet
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.components.ItemGastos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.components.ToolbarGastos


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CategoryGastosScreen(
    navController: NavHostController, viewModel: CategoryGastosViewModel,
    onBack: () -> Unit
) {
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val uiStateDefault by viewModel.uiStateDefault.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) { viewModel.getAllGastos() }

    when {
        uiState.isLoading -> CommonsLoadingScreen()
        uiState.items.isEmpty() -> {
            // Si la lista está vacía, mostrar
            viewModel.isActivatedFalse()
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BotonGastosIngresosPantallaGastos(
                    modifier = Modifier.fillMaxWidth()
                        .height(51.dp)
                        .padding(horizontal = 16.dp)
                )
                { tipoClase ->
                    if (tipoClase == CategoryTypeEnum.INGRESOS) {
                        navController.navigate(Routes.CategoriaIngresosScreen.route)
                    }
                }
                CommonsEmptyFloating { viewModel.onDismissSet(true) }
            }
        }
        uiState.isUpdateItem -> {
            CommonsLoadingData()
            CategoryGastosContent(
                uiState,
                uiStateDefault,
                viewModel,
                navController,
                sheetState
            )
            CommonsLoadingData()
        }
        else -> {
            CategoryGastosContent(
                uiState,
                uiStateDefault,
                viewModel,
                navController,
                sheetState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryGastosContent(
    uiState: ListUiState<UserCreateCategoryModel>,
    uiStateDefault: CategoryDefaultModel,
    viewModel: CategoryGastosViewModel,
    navController: NavHostController,
    sheetState: SheetState
) {
    BottomSheetScaffold(
        topBar = {
            ToolbarGastos(uiStateDefault, viewModel) { viewModel.borrandoLista() }
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
                            uiStateDefault,
                            viewModel
                        )
                    },
                )
            }
        },
        content = {
            ContentGastos(
                uiState.items,
                viewModel,
                navController,
                Modifier.padding(it)
            )
        }
    )
}

@Composable
fun ContentGastos(
    uiState: List<UserCreateCategoryModel>,
    viewModel: CategoryGastosViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    viewModel.isActivatedTrue()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                BotonGastosIngresosPantallaGastos(
                    Modifier.fillMaxWidth()
                        .height(51.dp)
                        .padding(horizontal = 16.dp)
                ) { tipoClase ->
                    if (tipoClase == CategoryTypeEnum.INGRESOS) {
                        navController.navigate(Routes.CategoriaIngresosScreen.route)
                    }
                }
            }

            items(uiState.size) { nuevoItem ->
                val item = uiState[nuevoItem]
                ItemGastos(item = item, viewModel)
            }
        }

        FloatingActionButton(
            onClick = { viewModel.onDismissSet(true) },
            modifier = modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(10.dp)
                .zIndex(1f)
        )
        { Icon(Icons.Default.Add, contentDescription = null) }
    }
}