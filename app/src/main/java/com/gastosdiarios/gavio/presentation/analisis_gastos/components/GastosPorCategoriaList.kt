package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState
    ) {
        item {
           // Mostrar el gráfico aquí siempre, incluso si no hay datos
            BarGraphConfigCustom(viewModel)
        }
        item {
            Spacer(modifier = Modifier.padding(16.dp))
            // Mostrar la categoría con más gastos si hay datos disponibles
            if (uiStateList.items.isNotEmpty()) {
                CategoriaConMasGastos(uiStateList = uiStateList.items,viewModel)
            } else {
                // Mostrar "Sin categoría" y total gastado "0.0" cuando no hay datos
                val sinCategoria = GastosPorCategoriaModel(
                    uid = "0",
                    title = "Sin categoría",
                    icon = "",
                    totalGastado = 0.0
                )
                CategoriaConMasGastos(uiStateList = listOf(sinCategoria), viewModel)
            }
        }
        // Mostrar mensaje o indicador visual cuando la lista está vacía
        if (transaccionesRevertidas.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 50.dp)) {
                    Text(
                        text = stringResource(R.string.no_hay_transacciones_disponibles),
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        } else {
            items(transaccionesRevertidas.size) { index ->
                val itemCategory = transaccionesRevertidas[index]
                ItemCategory(uiState = itemCategory, uiStateList = uiStateList.items, viewModel)
            }
        }

    }
    LaunchedEffect(uiStateList.items.size) {
        // muestra el ultimo elemento agregado en la parte superior
        listState.scrollToItem(index = 0)
    }
}