package com.gastosdiarios.gavio.presentation.transaction

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.UiStateList
import com.gastosdiarios.gavio.domain.model.Action
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.DialogDelete
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero
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
    val data by viewModel.dataList.collectAsState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    snackbarMessage?.let { messageResId ->
        val context = LocalContext.current
        LaunchedEffect(snackbarMessage) {
            isShowSnackbar.showSnackbar(context.getString(messageResId))
            viewModel.resetSnackbarMessage()
        }
    }

    val actions = listOf(
        Action(
            icon = Icons.Default.Create,
            contentDescription = "editar",
            onClick = { viewModel.isCreateTrue() }
        ),
        Action(
            icon = Icons.Default.Delete,
            contentDescription = "delete",
            onClick = { viewModel.isDeleteTrue() }
        )
    )
    BottomSheetScaffold(
        topBar = {
            TopAppBarOnBack(
                title = if (data.selectedItems.isNotEmpty()) "${data.selectedItems.size}"
                else {
                    stringResource(id = R.string.toolbar_registro_gastos)
                },
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = onBack,
                actions = {
                    if (data.selectionMode && data.selectedItems.size > 1) {
                        IconButton(onClick = { viewModel.deleteItemSelected() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete items"
                            )
                        }
                    } else if (data.selectionMode && data.selectedItems.size == 1) {
                        actions.forEach {
                            IconButton(onClick = it.onClick) {
                                Icon(
                                    imageVector = it.icon,
                                    contentDescription = it.contentDescription
                                )
                            }
                        }
                    }
                }
            )
        },
        sheetMaxWidth = 0.dp,
        sheetContent = {
            when {
                data.isCreate -> {
                    val item = data.selectedItems.firstOrNull() ?: TransactionModel()

                    ModalBottomSheet(onDismissRequest = { viewModel.isCreateFalse() },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        content = {
                            ContentBottomSheetTransaction(
                                item = item,
                                onDismiss = { viewModel.clearSelection(item) },
                                viewModel
                            )
                        }
                    )
                }
            }
        },
        content = { paddingValues ->
            PullToRefreshBox(
                state = rememberPullToRefreshState(),
                isRefreshing = isRefreshing.isRefreshing,
                onRefresh = { viewModel.refreshData() },
                modifier = Modifier.padding(paddingValues)
            ) {

                when (uiState) {
                    UiStateList.Loading -> {
                        CommonsLoadingScreen(modifier = Modifier.fillMaxSize())
                    }

                    UiStateList.Empty -> { CommonsIsEmpty() }

                    is UiStateList.Success -> {
                        val list: List<TransactionModel> =
                            (uiState as UiStateList.Success<TransactionModel>).data
                        ContentList(
                            viewModel,
                            list = list,
                            showCommonsLoadingData = data.updateItem
                        )
                    }

                    is UiStateList.Error -> {

                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    )

    DialogDelete(data.isDelete, onDismiss = { viewModel.isDeleteFalse() },
        onConfirm = { viewModel.deleteItemSelected() }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContentList(
    viewModel: TransactionsViewModel,
    list: List<TransactionModel>,
    showCommonsLoadingData: Boolean
) {
    val data by viewModel.dataList.collectAsState()

    val listState = rememberLazyListState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        //Agrupa los elementos de uiState.items por fecha.
        val groupItems = list.groupBy { it.date }.entries.sortedByDescending { it.key }
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

                ItemTransactions(
                    item,
                    viewModel,
                    isSelect = isSelect,
                    onClick = { viewModel.onClick(item) },
                    onLongClick = { viewModel.onLongClick(item) }
                )
            }
        }
    }

    LaunchedEffect(list.lastOrNull()) {
        // muestra el ultimo elemento agregado en la parte superior
        listState.scrollToItem(index = 0)
    }

    if (showCommonsLoadingData) {
        CommonsLoadingData()
    }
}

@Composable
fun ContentBottomSheetTransaction(
    item: TransactionModel,
    onDismiss: () -> Unit,
    viewModel: TransactionsViewModel
) {
    var cantidadIngresada by remember { mutableStateOf(item.cash.toString()) }
    var description by remember { mutableStateOf(item.subTitle.toString()) }
    val focusRequester = remember { FocusRequester() }

    val spaciness = 16.dp
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.padding(spaciness))

        TextFieldDinero(
            cantidadIngresada,
            Modifier.fillMaxWidth(),
            focusRequester = focusRequester
        ) { nuevoValor ->
            cantidadIngresada = nuevoValor
        }
        Spacer(modifier = Modifier.padding(spaciness))
        Text(text = stringResource(id = R.string.descripcion))
        //Description
        TextFieldDescription(
            description = description,
            modifier = Modifier.fillMaxWidth()
        ) { newDescription ->
            description = newDescription
        }
        Spacer(modifier = Modifier.size(150.dp))

        Button(
            onClick = {
                // Actualizar dinero y descripción
                viewModel.updateItem(
                    title = item.title.orEmpty(),
                    nuevoValor = cantidadIngresada,
                    description = description,
                    item = item
                )
                onDismiss()
                cantidadIngresada = ""
                description = ""
            }, modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = cantidadIngresada.isNotEmpty()
        ) {
            Text(text = stringResource(id = R.string.guardar))
        }
        Spacer(modifier = Modifier.size(24.dp))
    }
}