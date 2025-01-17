package com.gastosdiarios.gavio.presentation.create_gastos_default

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.presentation.transaction.components.ToolbarTransactions

@Composable
fun CreateGastosDefaultScreen(
    viewModel: CreateGastosDefaultViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.transactionUiState.collectAsState()
    Scaffold(topBar = { ToolbarTransactions() }, floatingActionButton = {
        FloatingActionButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }, content = { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(uiState.items, key = { it.uid!! }) {
                ReplyEmailListItem(item = it)
            }
        }
    })
}


