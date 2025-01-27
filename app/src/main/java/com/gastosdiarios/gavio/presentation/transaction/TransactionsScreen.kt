package com.gastosdiarios.gavio.presentation.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.DialogDelete
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero
import com.gastosdiarios.gavio.presentation.transaction.components.ItemFecha
import com.gastosdiarios.gavio.presentation.transaction.components.ItemTransactions
import com.gastosdiarios.gavio.utils.DateUtils
import kotlinx.serialization.json.JsonNull.content

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
    val selectionMode: Boolean by viewModel.selectionMode.collectAsState()
    val selectedItems: List<TransactionModel> by viewModel.selectedItems.collectAsState()
    val isDelete: Boolean by viewModel.isDelete.collectAsState()
    val isCreate: Boolean by viewModel.isCreate.collectAsState()

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
                title = if(selectionMode && selectedItems.size == 1){
                    stringResource(id = R.string.toolbar_registro_gastos)
                }
                else if (selectionMode && selectedItems.size > 1){
                   "${selectedItems.size}"
                }else{
                    stringResource(id = R.string.toolbar_registro_gastos)
                }
               ,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onBack = onBack,
                actions = {
                    if (selectionMode && selectedItems.size > 1) {
                        IconButton(onClick = { viewModel.deleteItemSelected() }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                    } else if (selectionMode && selectedItems.size == 1) {
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
                Content(isLoading, uiState, viewModel, Modifier.padding(paddingValues))
            }
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    )

    DialogDelete(isDelete, onDismiss = { viewModel.isDeleteFalse() },
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
    isLoading: Boolean,
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel,
    modifier: Modifier
) {
    when {
        isLoading -> CommonsLoadingScreen(modifier = modifier.fillMaxSize())
        uiState.items.isEmpty() -> CommonsIsEmpty()
        uiState.isUpdateItem -> {
            ContentList(uiState, viewModel)
            CommonsLoadingData()
        }

        else -> {
            ContentList(uiState, viewModel)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentList(
    uiState: ListUiState<TransactionModel>,
    viewModel: TransactionsViewModel
) {
    val selectedItem by viewModel.selectedItems.collectAsState()
    val listState = rememberLazyListState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
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
                key = { it.uid ?: it.hashCode() }
            ) { item ->
                val isSelect = selectedItem.any { it.uid == item.uid }

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

    LaunchedEffect(uiState.items.lastOrNull()) {
        // muestra el ultimo elemento agregado en la parte superior
        listState.scrollToItem(index = 0)
    }

}