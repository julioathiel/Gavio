package com.gastosdiarios.gavio.presentation.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.transaction.components.ItemFecha
import com.gastosdiarios.gavio.presentation.transaction.components.ItemTransactions
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


    Scaffold(
        topBar = {
            TopAppBarOnBack(
                title = stringResource(id = R.string.toolbar_registro_gastos),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onBack = onBack
            )
        },
        content = { paddingValues ->
            PullToRefreshBox(
                state = statePullToRefresh,
                isRefreshing = isRefreshing.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.padding(paddingValues)
            ) {
                Content(isLoading, uiState, viewModel, Modifier.padding(paddingValues))
            }
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    )
}

@Composable
fun Content(
    isLoading: Boolean,
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    when {
        isLoading -> CommonsLoadingScreen(modifier = modifier.fillMaxSize())
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentList(
    listState: LazyListState,
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState
        ) {
            //Agrupa los elementos de uiState.items por fecha.
            val groupItems = uiState.items.groupBy { it.date }.entries.sortedByDescending { it.key }
            groupItems.forEach { (date, itemsForDate) ->
                // Encabezado para cada grupo de fecha
                stickyHeader {
                    val datePersonalizada = DateUtils.converterFechaPersonalizada(date ?: "")
                    ItemFecha(fecha = datePersonalizada)
                }

                val sortedItems = itemsForDate.sortedByDescending { it.index }

                items(
                    items = sortedItems,
                    key = { it.uid!! }
                ) { item ->
                    ItemTransactions(item, uiState.items, viewModel)
                }
            }
        }

        LaunchedEffect(uiState.items.lastOrNull()) {
            // muestra el ultimo elemento agregado en la parte superior
            listState.scrollToItem(index = 0)
        }
    }
}