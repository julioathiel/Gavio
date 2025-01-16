package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosDefaultViewModel

@Composable
fun ContentListGastosprogramados(
    uiState: ListUiState<GastosProgramadosModel>,
    viewModel: CreateGastosDefaultViewModel,
    modifier: Modifier,
) {
    val selectedIndices by viewModel.selectedIndices.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    val context = LocalContext.current
    Box(modifier.fillMaxSize()) {
        LazyColumn {
            items(uiState.items, key = { it.uid!! }) { item ->
                val index = uiState.items.indexOf(item)
                val isSelected = index in selectedIndices
                ItemGastosProgramados(
                    item = item,
                    isSelected,
                    onClick = {
                        if (isSelectionMode) {
                            viewModel.toggleSelection(index)

                            // Verifica si es el último seleccionado
                            if (selectedIndices.lastOrNull() == index && selectedIndices.size == 1) {
                                viewModel.isActivatedFalse()
                            }
                        } else {
                            Toast.makeText(context, "index $index", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onLongItemClick = {
                        viewModel.toggleSelection(index)
                        viewModel.isActivatedTrue()
                    },
                    viewModel = viewModel
                )
            }
        }
        FloatingActionButton(
            onClick = {
                viewModel.setCurrentMode(Modo.AGREGAR)
                viewModel.onDismissSet(true)
            },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(16.dp)
                .zIndex(1f)
        )
        { Icon(Icons.Default.Add, contentDescription = null) }
    }
}