package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ContentBottomSheetGastosProgramados
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.DialogDelete
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ReplyListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGastosProgramadosScreen(
    viewModel: CreateGastosProgramadosViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.gastosProgramadosUiState.collectAsState()
    val isLoading: Boolean by viewModel.isLoading.collectAsState()
    val selectionMode: Boolean by viewModel.selectionMode.collectAsState()
    val selectedItems: List<GastosProgramadosModel> by viewModel.selectedItems.collectAsState()
    val isCreate by viewModel.isCreate.collectAsState()
    val isDelete by viewModel.isDelete.collectAsState()

    BottomSheetScaffold(
        topBar = {
            TopAppBarOnBack(
                title = "Gastos programados",
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = onBack,
                actions = {
                    if (selectionMode && selectedItems.size > 1) {
                        IconButton(onClick = {
                            selectedItems.forEach { item ->
                                viewModel.deleteItemSelected(item)
                            }
                        }) {
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
        sheetMaxWidth = 0.dp,
        sheetContent = {
            when {
                isCreate -> {

                    val item = selectedItems.firstOrNull() ?: GastosProgramadosModel()
                    val modo =
                        if (selectedItems.size == 1) Modo.EDITAR else Modo.AGREGAR
                    ModalBottomSheet(onDismissRequest = { viewModel.isCreateFalse() },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        content = {
                            ContentBottomSheetGastosProgramados(
                                item = item,
                                onDismiss = { viewModel.clearSelection(item) },
                                categoryTypes = TipoTransaccion.GASTOS,
                                viewModel = viewModel,
                                modo = modo
                            )
                        }
                    )
                }
            }
        },
        content = { paddingValues ->

            when {
                isLoading -> {
                    CommonsLoadingScreen(Modifier.fillMaxSize())
                }

                uiState.items.isEmpty() -> {
                    CommonsEmptyFloating(
                        onClick = { viewModel.isCreateTrue() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                uiState.isUpdateItem -> {
                    GastosProgramadosListContent(
                        uiState,
                        viewModel,
                        paddingValues,
                        selectionMode,
                        showCommonsLoadingData = true
                    )
                }

                else -> {
                    GastosProgramadosListContent(
                        uiState,
                        viewModel,
                        paddingValues,
                        selectionMode,
                        showCommonsLoadingData = false
                    )
                }
            }
        }
    )

    DialogDelete(isDelete, onDismiss = { viewModel.isDeleteFalse() },
        onConfirm = {
            selectedItems.forEach { item ->
                viewModel.deleteItemSelected(item)
            }
        }
    )

}


@Composable
fun GastosProgramadosListContent(
    uiState: ListUiState<GastosProgramadosModel>,
    viewModel: CreateGastosProgramadosViewModel,
    paddingValues: PaddingValues,
    selectionMode: Boolean,
    showCommonsLoadingData: Boolean
) {
    val selectedItem by viewModel.selectedItems.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        val expandedItem by viewModel.expandedItem.collectAsState()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.items, key = { it.uid ?: it.hashCode() }) { item ->
                val isSelected = selectedItem.any { it.uid == item.uid }
                ReplyListItem(
                    item = item,
                    isSelected = isSelected,
                    expandedItem,
                    onClick = {
                        viewModel.onClickGastosProgramados(item)
                    },
                    onLongClick = {
                        viewModel.onLongClickGastosProgramados(item)
                    }
                )
            }
        }
        //si ahy un elemento seleccionado, desaparece el boton de agregar
        if (!selectionMode) {
            FloatingActionButton(
                onClick = { viewModel.isCreateTrue() },
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        dimensionResource(id = R.dimen.padding_medium)
                    ),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "agregar"
                )
            }
        }
    }
    if (showCommonsLoadingData) {
        CommonsLoadingData()
    }
}