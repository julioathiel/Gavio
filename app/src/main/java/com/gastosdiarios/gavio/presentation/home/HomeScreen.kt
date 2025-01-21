package com.gastosdiarios.gavio.presentation.home


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.presentation.home.components.AddTransactionDialog
import com.gastosdiarios.gavio.presentation.home.components.BodyHeader
import com.gastosdiarios.gavio.presentation.home.components.CardBotonRegistro
import com.gastosdiarios.gavio.presentation.home.components.CountDate
import com.gastosdiarios.gavio.presentation.home.components.HorizontalPagerWithCards
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import kotlinx.serialization.json.JsonNull.content
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

    val uiState by viewModel.homeUiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

    PullToRefreshBox(
        state = statePullToRefresh,
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData() },
        modifier = modifier,
        content = {
            ContentHomeScreen(
                uiState,
                viewModel,
                navController,
                navigateToMovimientosScreen,
            )
        }
    )
}

@Composable
fun ContentHomeScreen(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    navController: NavController,
    navigateToMovimientos: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(Modifier.size(30.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "cargando datos...",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "espere un momento",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
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
                CountDate(Modifier.fillMaxWidth(), viewModel, uiState.fechaElegida)
                CommonsSpacer(height = 4.dp, width = 0.dp)
                NuevoMes(viewModel, uiState.showNuevoMes)
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
                    uiState.mostrandoDineroTotalIngresos,
                    uiState.mostrandoDineroTotalGastos
                )
                CommonsSpacer(height = 16.dp, width = 0.dp)
                HorizontalDivider()
                Spacer(modifier = Modifier.height(100.dp))
                AddTransactionDialog(
                    modifier = Modifier.fillMaxWidth(),
                    showTransaction = uiState.showTransaction,
                    onDismiss = { viewModel.onDialogClose() },
                    viewModel, navController
                )
            }
        }
    }
}

suspend fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String
) {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = null,
        duration = SnackbarDuration.Short
    )
}