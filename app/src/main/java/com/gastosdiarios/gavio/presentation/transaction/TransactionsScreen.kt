package com.gastosdiarios.gavio.presentation.transaction

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.transaction.components.ItemTransactions
import com.gastosdiarios.gavio.utils.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    //al presion el boton fisico de retoceso, se dirige a la pantalla de configuracion
    //BackHandler { onBack() }

    val uiState by viewModel.transactionUiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isShowSnackbar = remember { SnackbarHostState() }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

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
                title = stringResource(id = R.string.toolbar_all_expenses),
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                onBack()
            }
        },
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
    val isLoading by viewModel.isLoading.collectAsState()

    when {
        isLoading -> CommonsLoadingScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )

        uiState.items.isEmpty() -> CommonsIsEmpty(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )

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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = ScrollableDefaults.flingBehavior(),
            state = listState
        ) {
            // Agrupa los elementos por fecha y ordena los grupos por fecha descendente
            val groupedItems =
                uiState.items.groupBy { it.date }.entries.sortedByDescending { it.key }

            // Itera sobre los grupos y muestra un encabezado fijo para cada grupo
            groupedItems.forEach { (date, transactions) ->
                stickyHeader {
                    Log.d("fecha actual", "date: $date") //2025-01-08
                    val fechaActual: LocalDate = DateUtils.obtenerFechaActual() //2025-01-09

                    val fechaABarra = DateUtils.converterFechaABarra(date?: "") //2025-01-09
                    val dateCurrent: String = DateUtils.converterFechaABarra(fechaActual.toString()) //09/01/2025

                    val datePersonalizada = DateUtils.converterFechaPersonalizada(fechaABarra) //30 Ene. 2025
                    Log.d("fecha actual", "datePersonalizada : $datePersonalizada")
                    val textDate = when (fechaABarra) {
                        dateCurrent -> "hoy"
                        DateUtils.obtenerFechaAyer() -> "ayer"
                        else -> datePersonalizada
                    }


                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 16.dp)
                            .height(40.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = textDate,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.padding(end = 16.dp))
                    }
                }

                // Muestra los elementos de transacción para el grupo actual, ordenados por índice
                items(transactions.sortedByDescending { it.index }, key = { it.uid!! }) { item ->
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