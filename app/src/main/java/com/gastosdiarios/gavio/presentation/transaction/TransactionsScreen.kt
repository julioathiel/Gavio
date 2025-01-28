package com.gastosdiarios.gavio.presentation.transaction

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.DialogDelete
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

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val isShowSnackbar = remember { SnackbarHostState() }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()
    val data by viewModel.dataList.collectAsState()

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
                title = if (data.selectionMode && data.selectedItems.size == 1) {
                    stringResource(id = R.string.toolbar_registro_gastos)
                } else if (data.selectionMode && data.selectedItems.size > 1) {
                    "${data.selectedItems.size}"
                } else {
                    stringResource(id = R.string.toolbar_registro_gastos)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onBack = onBack,
                actions = {
                    if (data.selectionMode && data.selectedItems.size > 1) {
                        IconButton(onClick = { viewModel.deleteItemSelected() }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                    } else if (data.selectionMode && data.selectedItems.size == 1) {
                        IconButton(onClick = { viewModel.isCreateTrue() }) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "delete")
                        }

                        IconButton(onClick = {
                            viewModel.isDeleteTrue()
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                    }
                }
            )
        },
        content = { paddingValues ->
            PullToRefreshBox(
                state = statePullToRefresh,
                isRefreshing = isRefreshing.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.padding(paddingValues)
            ) {
                Content(viewModel, Modifier.padding(paddingValues))
            }
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    )

    DialogDelete(data.isDelete, onDismiss = { viewModel.isDeleteFalse() },
        onConfirm = { viewModel.deleteItemSelected() }
    )

    //        //si presiona editar se abrira otro dialogo para editar
//        if (showConfirmationEditar) {
//            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//            ModalBottomSheet(
//                onDismissRequest = {
//                    showConfirmationEditar = false
//                    showConfirmationDialog = false
//                },
//                sheetState = sheetState,
//                content = {
//
//                    var cantidadIngresada by remember { mutableStateOf(item.cash.toString()) }
//                    var description by remember { mutableStateOf(item.subTitle.toString()) }
//
//                    val spaciness = 16.dp
//                    Column(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp)
//                    ) {
//                        Spacer(modifier = Modifier.padding(spaciness))
//
//                        TextFieldDinero(
//                            cantidadIngresada,
//                            Modifier.fillMaxWidth(),
//                            focusRequester = focusRequester
//                        ) { nuevoValor ->
//                            cantidadIngresada = nuevoValor
//                        }
//                        Spacer(modifier = Modifier.padding(spaciness))
//                        Text(text = stringResource(id = R.string.descripcion))
//                        //Description
//                        TextFieldDescription(
//                            description = description,
//                            modifier = Modifier.fillMaxWidth()
//                        ) { newDescription ->
//                            description = newDescription
//                        }
//                        Spacer(modifier = Modifier.size(150.dp))
//
//                        Button(
//                            onClick = {
//                                // Actualizar dinero y descripción
//                                viewModel.updateItem(
//                                    title = item.title.orEmpty(),
//                                    nuevoValor = cantidadIngresada,
//                                    description = description,
//                                    valorViejo = item
//                                )
//                                showConfirmationEditar = false
//                                cantidadIngresada = ""
//                                description = ""
//                            }, modifier = Modifier
//                                .fillMaxWidth()
//                                .height(50.dp),
//                            enabled = cantidadIngresada.isNotEmpty()
//                        ) {
//                            Text(text = stringResource(id = R.string.guardar))
//                        }
//                        Spacer(modifier = Modifier.size(24.dp))
//                    }
//                }
//            )
//        }
}

@Composable
fun Content(
    viewModel: TransactionsViewModel,
    modifier: Modifier
) {
    val loading by viewModel.loading.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    when {
        loading -> {
            CommonsLoadingScreen(modifier = modifier.fillMaxSize())
        }

        uiState.empty -> { CommonsIsEmpty() }

        uiState.update -> {
            ContentList(viewModel)
            CommonsLoadingData()
        }

        else -> {
            ContentList(viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentList(viewModel: TransactionsViewModel) {
    val data by viewModel.dataList.collectAsState()
    val list by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        //Agrupa los elementos de uiState.items por fecha.
        val groupItems = list.items.groupBy { it.date }.entries.sortedByDescending { it.key }
        groupItems.forEach { (date, itemsForDate) ->
            // Encabezado para cada grupo de fecha
            stickyHeader {
                val datePersonalizada = DateUtils.converterFechaPersonalizada(date ?: "")
                ItemFecha(fecha = datePersonalizada)
            }

            val sortedItems = itemsForDate.sortedByDescending { it.index }

            items(
                items = sortedItems,
                key = { it.uid ?: it.hashCode() }
            ) { item ->
                val isSelect = data.selectedItems.any { it.uid == item.uid }
                Log.d("tagss", "ContentList: $item")
                ItemTransactions(
                    item,
                    viewModel,
                    isSelect = isSelect,
                    onClick = {
                        viewModel.onClick(item)
                    },
                    onLongClick = {
                        viewModel.onLongClick(item)
                    }
                )
            }
        }
    }

    LaunchedEffect(list.items.lastOrNull()) {
        // muestra el ultimo elemento agregado en la parte superior
        listState.scrollToItem(index = 0)
    }

}