package com.gastosdiarios.gavio.presentation.create_gastos_programados

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel

@Composable
fun CreateGastosProgramadosScreen(
    viewModel: CreateGastosProgramadosViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.gastosProgramadosUiState.collectAsState()
    val isLoading:Boolean by viewModel.isLoading.collectAsState()
    val selectionMode:Boolean by viewModel.selectionMode.collectAsState()
    val selectedItems:List<GastosProgramadosModel> by viewModel.selectedItems.collectAsState()
    Scaffold(topBar = {
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
                }
                else if (selectionMode && selectedItems.size == 1) {
                    IconButton(onClick = {
                       // viewModel.create(item)
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
    }, floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }, content = { paddingValues ->
        when {
            isLoading -> {
                CommonsLoadingScreen(Modifier.fillMaxSize())
            }

            else -> {
                // Estado para almacenar los elementos seleccionados
                val selectedItems by viewModel.selectedItems.collectAsState()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(uiState.items, key = { it.uid ?: it.hashCode() }) { item ->
                       // val isSelected = selectedItems.contains(item.uid)
                        val isSelected = selectedItems.any { it.uid == item.uid }
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
            }
        }
    }
    )
}


