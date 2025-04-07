package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.ErrorScreen
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ContentBottomSheet
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ItemCategory
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components.ToolbarCategoriasNuevas
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    // ... (Lógica común para ambas pantallas) ...
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
    BackHandler { onBack() }

    val uiStateDefault by viewModel.uiStateDefault.collectAsState()
    val showUpdate by viewModel.dataList.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    PantallaDeCategoriasCreadas(
        uiStateDefault = uiStateDefault,
        viewModel = viewModel,
        sheetState = sheetState,
        categoryTypes = if (pagerState.currentPage == 0) TipoTransaccion.INGRESOS else TipoTransaccion.GASTOS,
        onBack = { onBack() },
        content = { paddingValues ->

            Column(modifier = Modifier.padding(paddingValues)) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        },
                        text = { Text("Ingresos") }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        text = { Text("Gastos") }
                    )
                }
                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> { // Ingresos tab
                            val ingresosActions = object : CategoryActions {
                                override fun onEditClick(
                                    item: UserCreateCategoryModel,
                                    iconSelect: Int
                                ) {
                                    viewModel.selectedParaEditar(item, iconSelect)
                                }

                                override fun onDeleteClick(item: UserCreateCategoryModel) {
                                    viewModel.eliminarItemSelected(
                                        item,
                                        typeCategory = TipoTransaccion.INGRESOS
                                    )
                                }
                            }
                            StateContentCategoryIngresos(
                                viewModel,
                                ingresosActions,
                                Modifier.fillMaxSize(),
                                showUpdate
                            )
                        }

                        1 -> { // Gastos tab
                            val gastosActions = object : CategoryActions {
                                override fun onEditClick(
                                    item: UserCreateCategoryModel,
                                    iconSelect: Int
                                ) {
                                    viewModel.selectedParaEditar(item, iconSelect)
                                }

                                override fun onDeleteClick(item: UserCreateCategoryModel) {
                                    viewModel.eliminarItemSelected(
                                        item,
                                        typeCategory = TipoTransaccion.GASTOS
                                    )
                                }
                            }
                            StateContentCategoryGastos(
                                viewModel,
                                gastosActions,
                                Modifier.fillMaxSize(),
                                showUpdate
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun StateContentCategoryIngresos(
    viewModel: CategoryViewModel,
    ingresosActions: CategoryActions,
    modifier: Modifier,
    showUpdate: Boolean
) {
    val uiStateIngresos by viewModel.uiStateIngresos.collectAsStateWithLifecycle()


    when (uiStateIngresos) {
        UiStateList.Loading -> {
            CommonsLoadingScreen(Modifier.fillMaxSize())
        }

        is UiStateList.Error -> {
            ErrorScreen(
                uiStateIngresos as UiStateList.Error,
                retryOperation = {

                },
                modifier = modifier
            )
        }

        UiStateList.Empty -> {
            viewModel.isActivatedFalse()
            ContentCategoryEmpty(viewModel, modifier)
        }

        is UiStateList.Success -> {
            val list = (uiStateIngresos as UiStateList.Success<UserCreateCategoryModel>).data
            ListContentTypeCategory(
                list = list,
                viewModel = viewModel,
                ingresosActions,
                modifier = modifier,
                showUpdate = showUpdate
            )
        }
    }
}


@Composable
fun StateContentCategoryGastos(
    viewModel: CategoryViewModel,
    gastosActions: CategoryActions,
    modifier: Modifier,
    showUpdate: Boolean,
) {
    //Contenido para la pantalla de gastos
    val uiStateGastos by viewModel.uiStateGastos.collectAsStateWithLifecycle()

    when (uiStateGastos) {
        UiStateList.Loading -> {
            CommonsLoadingScreen(Modifier.fillMaxSize())
        }

        is UiStateList.Error -> {
            ErrorScreen(
                uiStateGastos as UiStateList.Error,
                retryOperation = {

                },
                modifier = modifier
            )
        }

        UiStateList.Empty -> {
            viewModel.isActivatedFalse()
            ContentCategoryEmpty(viewModel, modifier)
        }

        is UiStateList.Success -> {
            val list = (uiStateGastos as UiStateList.Success<UserCreateCategoryModel>).data
            ListContentTypeCategory(
                list = list,
                viewModel = viewModel,
                categoryActions = gastosActions,
                modifier = modifier,
                showUpdate = showUpdate
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
    categoryTypes: TipoTransaccion,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit = {},
) {
    BottomSheetScaffold(
        topBar = {
            ToolbarCategoriasNuevas(
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
    list: List<UserCreateCategoryModel>,
    viewModel: CategoryViewModel,
    categoryActions: CategoryActions,
    modifier: Modifier,
    showUpdate: Boolean
) {
    viewModel.isActivatedTrue()

    Box(modifier) {
        LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
            items(list.size) { nuevoItem ->
                val item = list[nuevoItem]
                ItemCategory(item = item, categoryActions = categoryActions)
            }
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(100.dp))
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

        if (showUpdate) {
            CommonsLoadingData()
        }
    }
}

@Composable
fun ContentCategoryEmpty(viewModel: CategoryViewModel, modifier: Modifier) {
    CommonsEmptyFloating(
        onClick = {
            viewModel.onDismissSet(true)
        }, modifier = modifier
    )
}