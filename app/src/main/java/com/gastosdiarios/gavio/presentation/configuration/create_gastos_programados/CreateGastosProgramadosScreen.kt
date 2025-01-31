package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.HorizontalDivider
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
import com.gastosdiarios.gavio.domain.model.Action
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
    val data by viewModel.dataList.collectAsState()
    val loading by viewModel.loading.collectAsState()
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
                title = if (data.selectedItems.isNotEmpty()) "${data.selectedItems.size}" else "Gastos programados",
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = onBack,
                actions = {
                    if (data.selectionMode && data.selectedItems.size > 1) {
                        IconButton(onClick = {
                            data.selectedItems.forEach { item ->
                                viewModel.deleteItemSelected(item)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
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

                    val item = data.selectedItems.firstOrNull() ?: GastosProgramadosModel()
                    val modo =
                        if (data.selectedItems.size == 1) Modo.EDITAR else Modo.AGREGAR
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
                loading -> {
                    CommonsLoadingScreen(Modifier.fillMaxSize())
                }

                uiState.items.isEmpty() -> {
                    CommonsEmptyFloating(
                        onClick = { viewModel.isCreateTrue() },
                        modifier = Modifier.padding(paddingValues)
                    )
                }

                uiState.update -> {
                    GastosProgramadosListContent(viewModel, showCommonsLoadingData = true)
                }

                else -> {
                    GastosProgramadosListContent(viewModel, showCommonsLoadingData = false)
                }
            }
        }
    )

    DialogDelete(data.isDelete, onDismiss = { viewModel.isDeleteFalse() },
        onConfirm = {
            data.selectedItems.forEach { item ->
                viewModel.deleteItemSelected(item)
            }
        }
    )

}


@Composable
fun GastosProgramadosListContent(
    viewModel: CreateGastosProgramadosViewModel,
    showCommonsLoadingData: Boolean
) {
    val uiState: ListUiState<GastosProgramadosModel> by viewModel.gastosProgramadosUiState.collectAsState()
    val data by viewModel.dataList.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            HorizontalDivider()
            LazyColumn {
                items(uiState.items, key = { it.uid ?: it.hashCode() }) { item ->
                    val isSelected = data.selectedItems.any { it.uid == item.uid }
                    ReplyListItem(
                        item = item,
                        isSelected = isSelected,
                        viewModel = viewModel,
                        onClick = { viewModel.onClick(item) },
                        onLongClick = { viewModel.onLongClick(item) }
                    )
                }
                item {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
        //si hay un elemento seleccionado, desaparece el boton de agregar
        if (!data.selectionMode) {
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