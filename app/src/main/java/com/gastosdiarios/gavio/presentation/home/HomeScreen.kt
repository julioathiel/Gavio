package com.gastosdiarios.gavio.presentation.home

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserData
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.presentation.home.components.AddTransactionDialog
import com.gastosdiarios.gavio.presentation.home.components.BodyHeader
import com.gastosdiarios.gavio.presentation.home.components.CardBotonRegistro
import com.gastosdiarios.gavio.presentation.home.components.CountDate
import com.gastosdiarios.gavio.presentation.home.components.HorizontalPagerWithCards
import com.gastosdiarios.gavio.presentation.home.components.NuevoMes
import com.google.android.play.integrity.internal.s
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
    val context = LocalContext.current
    val isShowSnackbar = remember { SnackbarHostState() }
    val uiState by viewModel.homeUiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()

    PullToRefreshBox(
        state = statePullToRefresh,
        isRefreshing = isRefreshing.isRefreshing,
        onRefresh = { viewModel.refreshData(context) },
        modifier = modifier,
        content = {
            ContentHomeScreen(
                context,
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
    context: Context,
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
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                BodyHeader(viewModel, onNavigationMovimientos = { navigateToMovimientos() })
                CommonsSpacer(height = 30.dp, width = 0.dp)

                HorizontalPagerWithCards(viewModel, Modifier.fillMaxWidth())
                CommonsSpacer(height = 30.dp, width = 0.dp)

                val up by viewModel.userPreferences.collectAsState()
                var s by remember { mutableStateOf(up.biometricSecurity ?: false) }

                Text(text = up.dateMax.toString())
                Text(text = up.hour.toString())
                Text(text = up.minute.toString())

                when (up.themeMode) {
                    ModeDarkThemeEnum.MODE_AUTO -> Text(text = up.themeMode?.name.toString())
                    ModeDarkThemeEnum.MODE_DAY -> Text(text = up.themeMode?.name.toString())
                    ModeDarkThemeEnum.MODE_NIGHT -> Text(text = up.themeMode?.name.toString())
                    null -> {}
                }
                LaunchedEffect(key1 = up) {
                    // Actualizar el estado local cuando cambien las preferencias
                    s = up.biometricSecurity ?: false
                }
                Log.d("HomeScreen", "ContentHomeScreen  s: $s")
                Switch(checked = s, onCheckedChange = { newState ->
                    s = newState
                    viewModel.updateBiometricSecurity(s)
                }
                )

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
                CountDate(context, Modifier.fillMaxWidth(), viewModel, uiState.fechaElegida)
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