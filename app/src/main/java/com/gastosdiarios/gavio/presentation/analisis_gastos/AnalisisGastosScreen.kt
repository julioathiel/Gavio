package com.gastosdiarios.gavio.presentation.analisis_gastos

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.presentation.analisis_gastos.components.GastosPorCategoriaList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisGastosScreen(
    modifier: Modifier = Modifier,
    viewModel: AnalisisGastosViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

    PullToRefreshBox(
        state = statePullToRefresh,
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData(context) },
        modifier = modifier,
        content = { ContentAnalisisGastos(viewModel, modifier) }
    )
}

@Composable
fun ContentAnalisisGastos(viewModel: AnalisisGastosViewModel, modifier: Modifier) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.getAllListGastos() }
    if (uiState.isLoading) {
        CommonsLoadingScreen(modifier = Modifier.fillMaxSize())
    } else {
        GastosPorCategoriaList(uiState, viewModel, Modifier.fillMaxSize())
    }
}