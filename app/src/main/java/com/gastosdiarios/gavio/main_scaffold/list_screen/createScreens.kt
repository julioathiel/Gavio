package com.gastosdiarios.gavio.main_scaffold.list_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.main_scaffold.data_class.Screen
import com.gastosdiarios.gavio.navigation.AcercaDeScreens
import com.gastosdiarios.gavio.navigation.ActualizarMaximoFechaScreen
import com.gastosdiarios.gavio.navigation.AjustesScreen
import com.gastosdiarios.gavio.navigation.CategoryScreen
import com.gastosdiarios.gavio.navigation.CongratulationsScreen
import com.gastosdiarios.gavio.navigation.CreateGastosProgramadosScreen
import com.gastosdiarios.gavio.navigation.ExportarDatosScreen
import com.gastosdiarios.gavio.navigation.NotificationsScreen
import com.gastosdiarios.gavio.navigation.TransactionsScreen
import com.gastosdiarios.gavio.navigation.UserProfileScreen
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosScreen
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationScreen
import com.gastosdiarios.gavio.presentation.home.HomeScreen

@Composable
fun createScreens(navController: NavHostController): List<Screen> {
    return listOf(
        Screen(
            nameToolbar = "Gavio",
            name = "Home",
            route = "HomeScreen",
            icon = R.drawable.ic_home_filled,
            content = { innerPadding ->
                HomeScreen(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.surface),
                    navController = navController,
                    navigateToMovimientosScreen = { navController.navigate(TransactionsScreen) }
                )
            }
        ),

        Screen(
            nameToolbar = "Analisis Gastos",
            name = "Analisis",
            route = "AnalisisGastosScreen",
            icon = R.drawable.ic_barra_filled,
            content = { innerPadding ->
                AnalisisGastosScreen(modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.surface))
            }
        ),
        Screen(
            nameToolbar = "Menu",
            name = "Menu",
            route = "ConfigurationScreen",
            icon = R.drawable.ic_menu,
            content = { innerPadding ->
                ConfigurationScreen(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).background(MaterialTheme.colorScheme.surface),
                    onToHomeScreen = { navController.navigate(com.gastosdiarios.gavio.navigation.HomeScreen) },
                    onToUserProfileScreen = { navController.navigate(UserProfileScreen) },
                    onToCategoriasGastosScreen = { navController.navigate(CategoryScreen) },
                    onToCreateGastosProgramadosScreen = {
                        navController.navigate(
                            CreateGastosProgramadosScreen
                        )
                    },
                    onToActualizarMaximoFechaScreen = {
                        navController.navigate(ActualizarMaximoFechaScreen)
                    },
                    onToRecordatorioScreen = { navController.navigate(NotificationsScreen) },
                    onToAcercaDeScreen = { navController.navigate(AcercaDeScreens) },
                    onToAjustesScreen = { navController.navigate(AjustesScreen) },
                    onToExportarDatosScreen = { navController.navigate(ExportarDatosScreen) },
                    onToCongratulationsScreen = { navController.navigate(CongratulationsScreen) }
                )
            }
        ),
    )
}