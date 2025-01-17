package com.gastosdiarios.gavio.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.credentials.CredentialManager

import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosScreen
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationScreen
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationViewModel
import com.gastosdiarios.gavio.presentation.configuration.acerca_de.AcercaDeScreen
import com.gastosdiarios.gavio.presentation.configuration.acerca_de.AcercaDeViewModel
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.ActualizarMaximoFechaScreen
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.ActualizarMaximoFechaViewModel
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesScreen
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesViewModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.CategoryGastosScreen
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.CategoryGastosViewModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.CategoryIngresosScreen
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.CategoryIngresosViewModel
import com.gastosdiarios.gavio.presentation.configuration.components.CongratulationsScreen
import com.gastosdiarios.gavio.presentation.configuration.user_profile.UserProfileScreen
import com.gastosdiarios.gavio.presentation.configuration.user_profile.UserProfileViewModel
import com.gastosdiarios.gavio.presentation.home.HomeScreen
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.presentation.home.components.MyAppTopLevelDestination
import com.gastosdiarios.gavio.presentation.home.components.TOP_LEVEL_DESTINATIONS
import com.gastosdiarios.gavio.presentation.transaction.TransactionsScreen
import com.gastosdiarios.gavio.presentation.transaction.TransactionsViewModel
import com.gastosdiarios.gavio.presentation.welcome.initial.InitialScreen
import com.gastosdiarios.gavio.presentation.welcome.initial.InitialViewModel
import com.gastosdiarios.gavio.presentation.welcome.login.LoginScreen
import com.gastosdiarios.gavio.presentation.welcome.login.LoginViewModel
import com.gastosdiarios.gavio.presentation.welcome.register.RegisterScreen
import com.gastosdiarios.gavio.presentation.welcome.register.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth

data class AppViewModels(
    val initialViewModel: InitialViewModel,
    val loginViewModel: LoginViewModel,
    val registerViewModel: RegisterViewModel,
    val homeViewModel: HomeViewModel,
    val transactionsViewModel: TransactionsViewModel,
    val configurationViewModel: ConfigurationViewModel,
    val userProfileViewModel: UserProfileViewModel,
    val actualizarMaximoFechaViewModel: ActualizarMaximoFechaViewModel,
    val ajustesViewModel: AjustesViewModel,
    val categoryIngresosViewModel: CategoryIngresosViewModel,
    val categoryGastosViewModel: CategoryGastosViewModel,
    val analisisGastosViewModel: AnalisisGastosViewModel,
    val acercaDeViewModel: AcercaDeViewModel
)

@Composable
fun MyAppContent(
    auth: FirebaseAuth,
    credentialManager: CredentialManager,
    navController: NavHostController,
    appViewModels: AppViewModels,
    selectedDestination: String,
    navigateTopLevelDestination: (MyAppTopLevelDestination) -> Unit
) {
    val loadingDestino = if (auth.currentUser?.email != null) {
        Routes.HomeScreen.route
    } else {
        Routes.LoginInitScreen.route
    }
    NavHost(
        navController = navController,
        startDestination = loadingDestino
    ) {
        composable(Routes.LoginInitScreen.route) {
            InitialScreen(
                auth = auth,
                credentialManager = credentialManager,
                viewModel = appViewModels.initialViewModel,
                navigateToRegister = { navController.navigate(Routes.RegisterScreen.route) },
                navigateToLogin = { navController.navigate(Routes.LoginScreen.route) },
                navigateToHomeScreen = { navController.navigate(Routes.HomeScreen.route) }
            )
        }
        composable(Routes.RegisterScreen.route) {
            RegisterScreen(appViewModels.registerViewModel,
                onToLogin = { email, password ->
                    navController.navigate(Routes.LoginScreen.route + "?email=${email}&password=${password}")
                }
            )
        }
        composable(
            Routes.LoginScreen.route + "?email={email}&password={password}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType; defaultValue = ""
                },
                navArgument("password") { type = NavType.StringType; defaultValue = "" }
            )
        ) { backStackEntry ->
            val email = requireNotNull(backStackEntry.arguments?.getString("email") ?: "")
            val password =
                requireNotNull(backStackEntry.arguments?.getString("password") ?: "")
            LoginScreen(appViewModels.loginViewModel, email, password,
                navigateToHome = { navController.navigate(Routes.HomeScreen.route) }
            )
        }
        composable(Routes.TransactionsScreen.route) {
            TransactionsScreen(
                appViewModels.transactionsViewModel,
                navigateToHomeScreen = { navController.navigate(Routes.HomeScreen.route) }
            )
        }
        composable(Routes.UserProfileScreen.route) {
            UserProfileScreen(viewModel = appViewModels.userProfileViewModel,
                onLoginInitScreen = { navController.navigate(Routes.LoginInitScreen.route) }
            )
        }
        composable(Routes.CategoriaGastosScreen.route) {
            CategoryGastosScreen(
                navController = navController,
                viewModel = appViewModels.categoryGastosViewModel,
                onBack = { navController.navigate(Routes.ConfigurationScreen.route) }
            )
        }
        composable(Routes.CategoriaIngresosScreen.route) {
            CategoryIngresosScreen(
                navController = navController,
                viewModel = appViewModels.categoryIngresosViewModel
            )
        }
        composable(Routes.ActualizarMaximoFechaScreen.route) {
            ActualizarMaximoFechaScreen(viewModel = appViewModels.actualizarMaximoFechaViewModel)
        }
        composable(Routes.AcercaDeScreen.route) {
            AcercaDeScreen(appViewModels.acercaDeViewModel)
        }
        composable(Routes.AjustesScreen.route) {
            AjustesScreen(
                viewModel = appViewModels.ajustesViewModel,
            )
//                onLoginInitScreen = { navController.navigate(Routes.LoginInitScreen.route) })
        }
        composable(Routes.CongratulationsScreen.route) {
            CongratulationsScreen(
                viewModel = appViewModels.configurationViewModel,
                onToHomeScreen = { navController.navigate(Routes.HomeScreen.route) })
        }

        composable(Routes.HomeScreen.route) {
            HomeScreen(
                viewModel = appViewModels.homeViewModel,
                navController = navController,
                navigateToMovimientos = { navController.navigate(Routes.TransactionsScreen.route) },
                bottomBar = {
                    MyBottomAppNavigation(
                        selectedDestination,
                        navigateTopLevelDestination
                    )
                }
            )
        }
        composable(Routes.AnalisisGastosScreen.route) {
            AnalisisGastosScreen(
                appViewModels.analisisGastosViewModel,
                bottomBar = {
                    MyBottomAppNavigation(
                        selectedDestination,
                        navigateTopLevelDestination
                    )
                }
            )
        }
        composable(Routes.ConfigurationScreen.route) {
            ConfigurationScreen(
                viewModel = appViewModels.configurationViewModel,
                onToHomeScreen = { navController.navigate(Routes.HomeScreen.route) },
                onToUserProfileScreen = { navController.navigate(Routes.UserProfileScreen.route) },
                onToCategoriasGastosScreen = { navController.navigate(Routes.CategoriaGastosScreen.route) },
                onToActualizarMaximoFechaScreen = { navController.navigate(Routes.ActualizarMaximoFechaScreen.route) },
                onToRecordatorioScreen = { navController.navigate(Routes.RecordatorioScreen.route) },
                onToAcercaDeScreen = { navController.navigate(Routes.AcercaDeScreen.route) },
                onToAjustesScreen = { navController.navigate(Routes.AjustesScreen.route) },
                onToCongratulationsScreen = { navController.navigate(Routes.CongratulationsScreen.route) },
                bottomBar = {
                    MyBottomAppNavigation(
                        selectedDestination,
                        navigateTopLevelDestination
                    )
                }
            )
        }
    }
}

@Composable
fun MyBottomAppNavigation(
    selectedDestination: String,
    navigateTopLevelDestination: (MyAppTopLevelDestination) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { destinations ->
            NavigationBarItem(
                selected = selectedDestination == destinations.route,
                onClick = { navigateTopLevelDestination(destinations) },
                icon = {
                    Icon(
                        painter = painterResource(id = destinations.selectedIcon),
                        contentDescription = stringResource(id = destinations.iconTextId)
                    )
                },
                label = { Text(text = stringResource(id = destinations.iconTextId)) }
            )
        }
    }
}