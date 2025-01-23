package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.ReplyProfileImage
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ContentBottomSheetGastosProgramados
import kotlinx.serialization.json.JsonNull.content

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
    var isEditar by remember { mutableStateOf(false) }

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
                                viewModel.delete(item)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                    } else if (selectionMode && selectedItems.size == 1) {
                        IconButton(onClick = {
                            isEditar = true
                        }) {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "delete")
                        }

                        IconButton(onClick = {
                            selectedItems.forEach { item ->
                                viewModel.delete(item)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "delete")
                        }
                    }
                }
            )
        },
        sheetMaxWidth = 0.dp,
        sheetContent = {
            when{
                isEditar -> {
                    val item = selectedItems.firstOrNull() ?: GastosProgramadosModel()
                    ModalBottomSheet(
                        onDismissRequest = { isEditar = false }, // Reset isEditar when dismissing
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        content = {
                            ContentBottomSheetGastosProgramados(
                                item = item,
                                onDismiss = {
                                    isEditar = false // Reset isEditar when dismissing
                                  //  viewModel.clearSelection()
                                },
                                categoryTypes = CategoryTypeEnum.GASTOS,
                                viewModel = viewModel,
                                modo = Modo.EDITAR // Set modo to EDITAR
                            )
                        }
                    )
                }
                isCreate -> {

                    val item = selectedItems.firstOrNull()
                        ?: GastosProgramadosModel() // Get selected item or create new one
                    val modo =
                        if (selectedItems.size == 1) Modo.EDITAR else Modo.AGREGAR // Set modo based on selectedItems size
                    ModalBottomSheet(onDismissRequest = { viewModel.isCreateFalse() },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        content = {
                            ContentBottomSheetGastosProgramados(
                                item = item,
                                onDismiss = {
                                    //viewModel.clearSelection()
                                }, // Clear selection when dismissing
                                categoryTypes = CategoryTypeEnum.GASTOS,
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

                else -> {
                    // Estado para almacenar los elementos seleccionados
                    val selectedItem by viewModel.selectedItems.collectAsState()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            items(uiState.items, key = { it.uid ?: it.hashCode() }) { item ->
                                val isSelected = selectedItem.any { it.uid == item.uid }
                                ReplyEmailListItem(
                                    item = item,
                                    isSelected = isSelected,
                                    onClick = {
                                        viewModel.onClickGastosProgramados(item)
                                    },
                                    onLongClick = {
                                        viewModel.onLongClickGastosProgramados(item)
                                    }
                                )
                            }
                        }
                        FloatingActionButton(
                            onClick = { viewModel.isCreateTrue() },
                            Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "agregar")
                        }
                    }

                }
            }
        }
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyEmailListItem(
    item: GastosProgramadosModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor =
            if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = item.icon?.toInt() ?: R.drawable.ic_info,
                    description = item.title ?: "",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.title ?: "",
                    )
                    Text(
                        text = item.cash ?: "",
                    )
                }
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .clip(CircleShape)

                ) {
//                    Icon(
//                        imageVector = Icons.Default.StarBorder,
//                        contentDescription = stringResource(id = R.string.description_favorite),
//                    )
                    if (isSelected)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notificacion),
                            contentDescription = "favorite"
                        )
                    else
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "favorite"
                        )
                }
            }

            Text(
                text = item.date ?: "",
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = item.subTitle ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
