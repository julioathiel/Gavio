package com.gastosdiarios.gavio.presentation.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.presentation.home.components.AddTransactionDialog
import com.gastosdiarios.gavio.presentation.home.components.BodyHeader
import com.gastosdiarios.gavio.presentation.home.components.CardBotonRegistro
import com.gastosdiarios.gavio.presentation.home.components.CountDate
import com.gastosdiarios.gavio.presentation.home.components.Header
import com.gastosdiarios.gavio.presentation.home.components.MyFAB
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    navigateToMovimientos: () -> Unit,
    onToCreateGastosDefault: () -> Unit
) {
    BackHandler { exitProcess(0) }

    val isShowSnackbar = remember { SnackbarHostState() }
    val uiState by viewModel.homeUiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

    PullToRefreshBox(
        state = statePullToRefresh,
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData() },
    ) {
        ContentHomeScreen(
            uiState,
            viewModel,
            navController,
            navigateToMovimientos,
            onToCreateGastosDefault
        )
    }
}


@Composable
fun ContentHomeScreen(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    navController: NavController,
    navigateToMovimientos: () -> Unit,
    onToCreateGastosDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(Modifier.size(30.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "cargando datos espere un momento",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState()),
            ) {
//                Header(viewModel = viewModel)
//                Spacer(modifier = Modifier.padding(vertical = 10.dp))
                BodyHeader(viewModel, onNavigationMovimientos = { navigateToMovimientos() })
                CommonsSpacer(height = 30.dp, width = 0.dp)

                Card(
                    modifier = Modifier
                        .padding(dimensionResource(id = R.dimen.padding_medium))
                        .height(100.dp)
                        .width(70.dp),
                    //shape = RoundedCornerShape(16.dp), // Ajusta la forma según tus necesidades
                    border = CardDefaults.outlinedCardBorder(),

                    ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .fillMaxSize()
                            .clickable { onToCreateGastosDefault() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar"
                        )
                    }
                }
                CountDate(Modifier.fillMaxWidth(), viewModel, uiState.fechaElegidaBarra)
                CommonsSpacer(height = 4.dp, width = 0.dp)
                NuevoMes(viewModel, uiState.showNuevoMes)
                CommonsSpacer(height = 30.dp, width = 0.dp)
                CardBotonRegistro(
                    uiState.mostrandoDineroTotalIngresos,
                    uiState.mostrandoDineroTotalGastos
                )
                AddTransactionDialog(
                    modifier = Modifier.fillMaxWidth(),
                    showDialogTransaction = uiState.showDialogTransaction,
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