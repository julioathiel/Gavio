package com.gastosdiarios.gavio.presentation.home


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.commons.ErrorScreen
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import com.gastosdiarios.gavio.presentation.home.components.AddTransactionDialog
import com.gastosdiarios.gavio.presentation.home.components.BodyHeader
import com.gastosdiarios.gavio.presentation.home.components.CardBotonRegistro
import com.gastosdiarios.gavio.presentation.home.components.CargandoDatos
import com.gastosdiarios.gavio.presentation.home.components.CountDate
import com.gastosdiarios.gavio.presentation.home.components.HorizontalPagerWithCards
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    navigateToMovimientosScreen: () -> Unit
) {
    BackHandler { exitProcess(0) }
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    PullToRefreshBox(
        state = rememberPullToRefreshState(),
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = modifier,
        content = {

            when (val state = uiState) {
                UiStateSingle.Loading -> {
                    CargandoDatos()
                }

                is UiStateSingle.Success -> {
                    Body(viewModel, navController, navigateToMovimientosScreen)
                }

                is UiStateSingle.Error -> {
                    ErrorScreen(
                        uiState = state,
                        retryOperation = { viewModel.refreshData() },
                        modifier
                    )
                }
            }
        }
    )
}

@Composable
fun Body(
    viewModel: HomeViewModel,
    navController: NavController,
    navigateToMovimientos: () -> Unit
) {
    val data by viewModel.homeUiState.collectAsState()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        BodyHeader(viewModel, onNavigationMovimientos = { navigateToMovimientos() })
        CommonsSpacer(height = 30.dp, width = 0.dp)

        HorizontalPagerWithCards(viewModel, Modifier.fillMaxWidth())
        CommonsSpacer(height = 30.dp, width = 0.dp)

        Text(
            "Fecha elegida",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
        CommonsSpacer(height = 16.dp, width = 0.dp)
        CountDate(Modifier.fillMaxWidth(), viewModel, data.fechaElegida)
        CommonsSpacer(height = 4.dp, width = 0.dp)
        NuevoMes(viewModel, data.showNuevoMes)
        CommonsSpacer(height = 30.dp, width = 0.dp)
        Text(
            "registro de progreso",
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(start = 16.dp),
            textAlign = TextAlign.Start
        )
        CommonsSpacer(height = 16.dp, width = 0.dp)
        CardBotonRegistro(
            data.mostrandoDineroTotalIngresos,
            data.mostrandoDineroTotalGastos
        )
        CommonsSpacer(height = 16.dp, width = 0.dp)
        HorizontalDivider()
        Spacer(modifier = Modifier.height(100.dp))
        AddTransactionDialog(
            modifier = Modifier.fillMaxWidth(),
            showTransaction = data.showTransaction,
            onDismiss = { viewModel.onDialogClose() },
            viewModel, navController
        )
    }
}


