package com.gastosdiarios.gavio.presentation.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.transaction.components.ItemFecha
import com.gastosdiarios.gavio.presentation.transaction.components.ItemTransactions
import com.gastosdiarios.gavio.presentation.transaction.components.ToolbarTransactions

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    navigateToHomeScreen: () -> Unit
) {
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
    BackHandler { navigateToHomeScreen() }

    val uiState by viewModel.transactionUiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isShowSnackbar = remember { SnackbarHostState() }

    snackbarMessage?.let { messageResId ->
        val context = LocalContext.current
        LaunchedEffect(snackbarMessage) {
            isShowSnackbar.showSnackbar(context.getString(messageResId))
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(Unit) { viewModel.getAllTransactions() }

    when {
        uiState.isLoading -> CommonsLoadingScreen()
        uiState.items.isEmpty() -> CommonsIsEmpty()
        uiState.isUpdateItem -> {
            CommonsLoadingData()
            Scaffold(
                topBar = { ToolbarTransactions() },
                content = { miPadding ->
                    Content(uiState.items, viewModel, Modifier.padding(miPadding))
                },
                snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
            )
            CommonsLoadingData()
        }

        else -> {
            Scaffold(
                topBar = { ToolbarTransactions() },
                content = { miPadding ->
                    Content(uiState.items, viewModel, Modifier.padding(miPadding))
                },
                snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
            )
        }
    }
}

@Composable
fun Content(
    uiStateList: List<TransactionModel>,
    viewModel: TransactionsViewModel,
    modifier: Modifier
) {
    val listState = rememberLazyListState()
    //agrupando las fechas que coincidan
    //agrupa y ordena las transacciones por fecha dejando el ultimo arriba
    val movimientosPorFecha = uiStateList.groupBy { it.date }

    //ordenando fechas por orden descendentes, esto creara que los dias mas recientes esten arriba
    val fechasOrdenadas = movimientosPorFecha.keys.sortedByDescending { it }
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            items(fechasOrdenadas.size, key = { it }) { index ->
                //composable que muestra la fecha
                val fecha = fechasOrdenadas[index]
                ItemFecha(fecha.toString())
                // Mostrar las transaciones correspondientes a la fecha actual
                val transactionPorDate = movimientosPorFecha[fecha] ?: emptyList()

                transactionPorDate.map { item ->
                    ItemTransactions(item, uiStateList, viewModel)
                }
            }
        }

        LaunchedEffect(uiStateList.lastOrNull()) {
            // O cualquier propiedad que indique un nuevo elemento
            // muestra el ultimo elemento agregado en la parte superior
            listState.scrollToItem(index = 0)
        }
    }
}