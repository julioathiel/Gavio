package com.gastosdiarios.gavio.presentation.create_gastos_programados

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.presentation.transaction.components.ToolbarTransactions

@Composable
fun CreateGastosProgramadosScreen(
    viewModel: CreateGastosProgramadosViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.gastosProgramadosUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    Scaffold(topBar = {
        TopAppBarOnBack(
            title = "Gastos programados",
            containerColor = MaterialTheme.colorScheme.surface,
            onBack = onBack
        )
    }, floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }, content = { paddingValues ->
        when{
            isLoading -> {
                CommonsLoadingScreen(Modifier.fillMaxSize())
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(uiState.items, key = { it.uid!! }) {
                        ReplyEmailListItem(item = it)
                    }
                }
            }
        }

    })
}


