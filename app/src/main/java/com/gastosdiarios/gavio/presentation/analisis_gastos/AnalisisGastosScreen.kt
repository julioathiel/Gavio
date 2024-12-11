package com.gastosdiarios.gavio.presentation.analisis_gastos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.CommonsToolbar
import com.gastosdiarios.gavio.presentation.analisis_gastos.components.GastosPorCategoriaList

@Composable
fun AnalisisGastosScreen(
    viewModel: AnalisisGastosViewModel,
    bottomBar: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            CommonsToolbar(
                title = stringResource(id = R.string.toolbar_analisis_de_gastos),
                colors = MaterialTheme.colorScheme.background
            )
        },
        bottomBar = { bottomBar() },
        containerColor = MaterialTheme.colorScheme.background,
        content = { paddingValues ->
            Content(viewModel, Modifier.padding(paddingValues))
        }
    )
}

@Composable
fun Content(viewModel: AnalisisGastosViewModel, modifier: Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.getAllListGastos() }
    if (uiState.isLoading) {
        CommonsLoadingScreen()
    }else{
        GastosPorCategoriaList(uiState, viewModel, modifier)
    }
}