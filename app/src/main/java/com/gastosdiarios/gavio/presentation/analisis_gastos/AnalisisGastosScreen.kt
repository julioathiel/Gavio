package com.gastosdiarios.gavio.presentation.analisis_gastos

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.bar_graph_custom.BarGraphConfigCustom
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.ErrorScreen
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.components.ItemCategoriaConMasGastos
import com.gastosdiarios.gavio.presentation.analisis_gastos.components.ItemCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalisisGastosViewModel = hiltViewModel()
) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

    PullToRefreshBox(
        state = statePullToRefresh,
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = modifier,
        content = { GastosPorCategoriaList(viewModel, Modifier.fillMaxSize()) }
    )
}

@Composable
fun GastosPorCategoriaList(
    viewModel: AnalisisGastosViewModel,
    modifier: Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showTwoColumns by viewModel.showTwoColumns.collectAsStateWithLifecycle()
    val columns = if (showTwoColumns) 2 else 1
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //title grafico del año
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Grafico del año",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        //grafico
        item(span = { GridItemSpan(maxLineSpan) }) {
            val uiStateListBarGraph by viewModel.listBarDataModel.collectAsStateWithLifecycle()
            // Mostrar el gráfico aquí siempre, incluso si no hay datos
            when (val state = uiStateListBarGraph) {
                UiStateList.Loading -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(358.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }

                UiStateList.Empty -> {
                    // Mostrar un mensaje al usuario en el caso de que no haya datos
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(358.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "No hay datos disponibles",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                is UiStateList.Success -> {
                    val list: List<BarDataModel> =
                        (uiStateListBarGraph as UiStateList.Success<BarDataModel>).data
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                        BarGraphConfigCustom(list)
                    }
                }

                is UiStateList.Error -> {
                    ErrorScreen(
                        uiState = state,
                        retryOperation = {},
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        //title lo mas gastado este mes
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.lo_mas_gastado_este_mes),
                modifier = Modifier.padding(top = 20.dp),
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        //card lo mas gastado este mes
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Mostrar la categoría con más gastos si hay datos disponibles
            when (uiState) {
                UiStateList.Loading -> {}

                UiStateList.Empty -> {
                    val sinCategoria = GastosPorCategoriaModel(
                        uid = "0",
                        title = "Sin categoría",
                        icon = "",
                        totalGastado = 0.0
                    )
                    ItemCategoriaConMasGastos(uiStateList = listOf(sinCategoria), viewModel)
                }

                is UiStateList.Success -> {
                    val list = (uiState as UiStateList.Success<GastosPorCategoriaModel>).data
                    ItemCategoriaConMasGastos(uiStateList = list, viewModel)
                }

                is UiStateList.Error -> {}
            }
        }
        //title gastos por categoria
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier.padding(top = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Gastos por categoria",
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = {
                    viewModel.setToggleTwoColumns(!showTwoColumns)
                }) {
                    if(showTwoColumns){
                        Icon(painterResource(R.drawable.ic_list), contentDescription = "list")
                    }else{
                        Icon(painterResource(R.drawable.ic_view_cozy), contentDescription = "gridView")
                    }
                }
            }
        }

        // Mostrar la lista de categorías con gastos si hay datos disponibles
        when (val state = uiState) {
            UiStateList.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CommonsLoadingScreen()
                }
            }

            UiStateList.Empty -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CommonsIsEmpty()
                }
            }

            is UiStateList.Success -> {
                val list = (uiState as UiStateList.Success<GastosPorCategoriaModel>).data
                items(list.size) { index ->
                    val (tertiaryContainer, onTertiary) = viewModel.getRandomColor(
                        isSystemInDarkTheme()
                    )
                    val itemCategory = list[index]

                    ItemCategory(
                        uiState = itemCategory,
                        uiStateList = list,
                        viewModel = viewModel,
                        tertiaryContainer = tertiaryContainer,
                        onTertiary = onTertiary
                    )
                }
            }

            is UiStateList.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ErrorScreen(
                        uiState = state,
                        retryOperation = { viewModel.refreshData() }, // Reintentar la operación
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}