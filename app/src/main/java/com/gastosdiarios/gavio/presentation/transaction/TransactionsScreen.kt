package com.gastosdiarios.gavio.presentation.transaction
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.transaction.components.ItemFecha
import com.gastosdiarios.gavio.presentation.transaction.components.ItemTransactions
import com.gastosdiarios.gavio.presentation.transaction.components.ToolbarTransactions
import com.gastosdiarios.gavio.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
    BackHandler { onBack() }

    val uiState by viewModel.transactionUiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isShowSnackbar = remember { SnackbarHostState() }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()
    val isLoading by viewModel.isLoading.collectAsState()
    snackbarMessage?.let { messageResId ->
        val context = LocalContext.current
        LaunchedEffect(snackbarMessage) {
            isShowSnackbar.showSnackbar(context.getString(messageResId))
            viewModel.resetSnackbarMessage()
        }
    }

    LaunchedEffect(Unit) { viewModel.getAllTransactions() }

    Scaffold(
        topBar = { ToolbarTransactions() },
        content = { paddingValues ->
            PullToRefreshBox(
                state = statePullToRefresh,
                isRefreshing = isRefreshing.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.padding(paddingValues)
            ) {
                Content(uiState, viewModel, Modifier.padding(paddingValues))
            }
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    )
}

@Composable
fun Content(
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    when {
        uiState.isLoading -> CommonsLoadingScreen(modifier = modifier.fillMaxSize())
        uiState.items.isEmpty() -> CommonsIsEmpty()
        uiState.isUpdateItem -> {
            ContentList(listState, uiState, viewModel)
            CommonsLoadingData()
        }

        else -> {
            ContentList(listState, uiState, viewModel)
        }
    }
}

@Composable
fun ContentList(
    listState: LazyListState,
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            //Agrupa los elementos de uiState.items por fecha.
            val list = uiState.items.groupBy { it.date }
                // Obtiene las entradas del mapa resultante, que son pares de clave-valor (fecha-lista de elementos).
                .entries.sortedByDescending {
                    it.key
                    // Ordena las entradas del mapa por fecha (clave) en orden descendente.
                }
                .flatMap { group -> //Aplana la colección de entradas del mapa en una sola lista.
                    // Ordena los elementos dentro de cada grupo usando compareByDescending y thenByDescending.
                    group.value.sortedWith(
                        compareByDescending<TransactionModel> { it.index }//Ordena primero por índice en orden descendente.
                            .thenByDescending {
                                it.date
                            }//Si hay elementos con el mismo índice, los ordena por fecha en orden descendente.
                    )
                }

            //Con este código, la lista list contendrá los elementos ordenados primero
            // por fecha de mayor a menor y luego por índice de mayor a menor
            // dentro de cada grupo de fecha.

            items(list, key = { it.uid!! }) { item ->
                val currentDate = item.date
                val dateABarra = DateUtils.converterFechaABarra(currentDate!!)
                val datePersonalizada = DateUtils.converterFechaPersonalizada(dateABarra)

                val isFirstItemForDate: Boolean =
                    list.asSequence() // Convertir a secuencia para usar funciones de secuencia
                        .filter { it.date == item.date } // Filtrar por fecha
                        .indexOfFirst { it.uid == item.uid } == 0 // Verificar si es el primer elemento con esa fecha y uid
                if (isFirstItemForDate) {
                    ItemFecha(fecha = datePersonalizada)
                }
                ItemTransactions(item, uiState.items, viewModel)
            }
        }

        LaunchedEffect(uiState.items.lastOrNull()) {
            // muestra el ultimo elemento agregado en la parte superior
            listState.scrollToItem(index = 0)
        }
    }
}