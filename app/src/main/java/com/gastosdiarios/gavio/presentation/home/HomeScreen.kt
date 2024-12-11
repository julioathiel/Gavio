package com.gastosdiarios.gavio.presentation.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.presentation.home.components.AddTransactionDialog
import com.gastosdiarios.gavio.presentation.home.components.BodyHeader
import com.gastosdiarios.gavio.presentation.home.components.CardBotonRegistro
import com.gastosdiarios.gavio.presentation.home.components.CountDate
import com.gastosdiarios.gavio.presentation.home.components.Header
import com.gastosdiarios.gavio.presentation.home.components.MyFAB
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import kotlin.system.exitProcess

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    navigateToMovimientos: () -> Unit,
    bottomBar: @Composable () -> Unit
) {
    BackHandler { exitProcess(0) }

    val isShowSnackbar = remember { SnackbarHostState() }
    val homeUiState by viewModel.homeUiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.calculandoInit() }

    if (homeUiState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(Modifier.size(30.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "cargando datos espere un momento", textAlign = TextAlign.Center)
        }
    } else if (homeUiState.isError) {
        Column {
            Text(text = "Error de conexión. Verifique su conexión a internet.", textAlign = TextAlign.Center)
            // Optional: Add a retry button
            Button(onClick = {
                viewModel.resetErrorState() // Reset error state in ViewModel
                viewModel.calculandoInit() // Trigger data reloading
            }) {
                Text("Reintentar")
            }
        }
    } else {
        Scaffold(
            floatingActionButton = {
                MyFAB(
                    diasRestantes = homeUiState.diasRestantes,
                    isShowSnackbar,
                    onDismiss = { viewModel.onShowDialogClickTransaction() }
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            snackbarHost = {
                SnackbarHost(hostState = isShowSnackbar)
            },
            bottomBar = { bottomBar() }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            ) {
                Header(viewModel = viewModel)
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                BodyHeader(viewModel, onNavigationMovimientos = { navigateToMovimientos() })
                CommonsSpacer(height = 30.dp, width = 0.dp)
                CountDate(Modifier.fillMaxWidth(), viewModel, homeUiState.fechaElegidaBarra)
                CommonsSpacer(height = 4.dp, width = 0.dp)
                NuevoMes(viewModel, homeUiState.showNuevoMes)
                CommonsSpacer(height = 30.dp, width = 0.dp)
                CardBotonRegistro(
                    homeUiState.mostrandoDineroTotalIngresos,
                    homeUiState.mostrandoDineroTotalGastos
                )
                AddTransactionDialog(
                    modifier = Modifier.fillMaxWidth(),
                    showDialogTransaction = homeUiState.showDialogTransaction,
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(Modifier.size(30.dp))
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "Cargando datos espere un momento...", textAlign = TextAlign.Center)
    }
}