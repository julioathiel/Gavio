package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.bar_graph_custom.BarGraphConfigCustom
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel

@Composable
fun GastosPorCategoriaList(
    uiStateList: ListUiState<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel,
    modifier: Modifier,
) {
    // Invertir el orden de la lista asi lo  que se grega va quedando arriba
    val transaccionesRevertidas = uiStateList.items.reversed()
    val uiStateListBarGraph by viewModel.listBarDataModel.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Grafico del año",
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            // Mostrar el gráfico aquí siempre, incluso si no hay datos
            if (uiStateListBarGraph.items.isNotEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    BarGraphConfigCustom(
                        viewModel
                    )
                }
            } else {
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
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = stringResource(R.string.lo_mas_gastado_este_mes),
                modifier = Modifier.padding(top = 20.dp),
                fontSize = 16.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            // Mostrar la categoría con más gastos si hay datos disponibles
            if (uiStateList.items.isNotEmpty()) {
                ItemCategoriaConMasGastos(uiStateList = uiStateList.items, viewModel)
            } else {
                // Mostrar "Sin categoría" y total gastado "0.0" cuando no hay datos
                val sinCategoria = GastosPorCategoriaModel(
                    uid = "0",
                    title = "Sin categoría",
                    icon = "",
                    totalGastado = 0.0
                )
                ItemCategoriaConMasGastos(uiStateList = listOf(sinCategoria), viewModel)
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = "Gastos por categoria",
                style = MaterialTheme.typography.titleMedium
            )
        }
        items(transaccionesRevertidas.size) { index ->
           val (tertiaryContainer, onTertiary)  = viewModel.getRandomColor(isSystemInDarkTheme())
            val itemCategory = transaccionesRevertidas[index]

            ItemCategory(
                modifier = modifier,
                uiState = itemCategory,
                uiStateList = uiStateList.items,
                viewModel = viewModel,
                tertiaryContainer = tertiaryContainer,
                onTertiary = onTertiary
            )
        }
    }
}

