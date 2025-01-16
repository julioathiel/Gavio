package com.gastosdiarios.gavio.presentation.home

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.gastosdiarios.gavio.presentation.home.components.HorizontalPagerWithCards
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    navigateToMovimientos: () -> Unit
) {
    BackHandler { exitProcess(0) }
    val context = LocalContext.current
    val uiState by viewModel.homeUiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    PullToRefreshBox(
        state = rememberPullToRefreshState(),
        modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData(context) },
    ) {
        ContentHomeScreen(
            context,
            modifier,
            uiState,
            viewModel,
            navController,
            navigateToMovimientos
        )
    }
}


@Composable
fun ContentHomeScreen(
    context: Context,
    modifier: Modifier,
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    navController: NavController,
    navigateToMovimientos: () -> Unit
) {
    val paddingMedium = dimensionResource(id = R.dimen.padding_medium)
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                BodyHeader(viewModel, onNavigationMovimientos = { navigateToMovimientos() })
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                HorizontalPagerWithCards(viewModel, modifier = Modifier.fillMaxSize())
                Spacer(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = stringResource(R.string.dias_que_restan),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = paddingMedium, start = paddingMedium)
                )
                CountDate(context, Modifier.fillMaxWidth(), viewModel, uiState.fechaElegida)
                CommonsSpacer(height = 4.dp, width = 0.dp)
                NuevoMes(viewModel, uiState.showNuevoMes)
                Text(
                    text = "Progreso del presupuesto",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(paddingMedium)
                )
                CardBotonRegistro(
                    uiState.mostrandoDineroTotalIngresos,
                    uiState.mostrandoDineroTotalGastos
                )
                AddTransactionDialog(
                    modifier = Modifier.fillMaxWidth(),
                    showAgregar = uiState.agregar,
                    onDismiss = { viewModel.onDialogClose() },
                    viewModel,
                    navController)

                Spacer(modifier = Modifier.padding(vertical = 20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.padding(vertical = 50.dp))
            }
        }
    }
}