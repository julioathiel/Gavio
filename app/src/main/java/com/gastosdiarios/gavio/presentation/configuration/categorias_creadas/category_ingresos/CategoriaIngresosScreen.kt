package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.navigation.Routes
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components.ButtonGastosIngresos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components.ContentBottomSheet
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components.ItemIngresos
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components.ToolbarCategories

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryIngresosScreen(
    navController: NavHostController, viewModel: CategoryIngresosViewModel
) {
    val onDismiss by viewModel.onDismiss
    val uiState: State<List<UserCreateCategoryModel>> = viewModel.uiState.collectAsState()
    val isLoading = viewModel.isLoading.value
    val isLoadingData = viewModel.isLoadingData.value
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BackHandler { navController.navigate(Routes.ConfigurationScreen.route) }

    LaunchedEffect(Unit) { viewModel.getAllIngresos() }

    if (isLoadingData) { CommonsLoadingData() }

    BottomSheetScaffold(topBar = {
        ToolbarCategories(viewModel) { viewModel.deleteAllList() }
    },
        sheetMaxWidth = 0.dp,
        sheetContent = {
            if (onDismiss) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onDismissSet(false) },
                    sheetState = sheetState,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Llama a ContentBottomSheet desde aquí
                            ContentBottomSheet(
                                onDismiss = { viewModel.onDismissSet(false) },
                                viewModel
                            )
                        }
                    },
                )
            }
        },
        content = {
            ContentIngresos(uiState, viewModel, navController, Modifier.fillMaxWidth().padding(it))
        }
    )
}

@Composable
fun ContentIngresos(
    uiState: State<List<UserCreateCategoryModel>>,
    viewModel: CategoryIngresosViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    if (uiState.value.isEmpty()) {
        // Si la lista está vacía, mostrar
        viewModel.isActivatedFalse()
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonGastosIngresos(modifier.height(51.dp).padding(horizontal = 16.dp)) { tipoClase ->
                if (tipoClase == CategoryTypeEnum.GASTOS) {
                    navController.navigate(Routes.CategoriaGastosScreen.route)
                }
            }
            CommonsEmptyFloating { viewModel.onDismissSet(true) }
        }
    } else {
        viewModel.isActivatedTrue()
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                ButtonGastosIngresos {
                    if (it == CategoryTypeEnum.GASTOS) {
                        navController.navigate(Routes.CategoriaGastosScreen.route)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                ListaNuevaCategoriaIngresos(newList = uiState, viewModel)
            }
            FloatingActionButton(
                onClick = { viewModel.onDismissSet(true) },
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(10.dp)
                    .zIndex(1f)
            )
            { Icon(Icons.Default.Add, contentDescription = null) }
        }
    }

}

@Composable
fun ListaNuevaCategoriaIngresos(
    newList: State<List<UserCreateCategoryModel>>,
    viewModel: CategoryIngresosViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        items(newList.value.size) { nuevoItem ->
            val item = newList.value[nuevoItem]
            ItemIngresos(item = item, viewModel)
        }
    }
}