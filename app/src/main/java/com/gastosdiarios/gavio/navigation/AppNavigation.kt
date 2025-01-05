package com.gastosdiarios.gavio.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gastosdiarios.gavio.MainActivity
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.CommonsToolbar
import com.gastosdiarios.gavio.data.commons.ConnectivityErrorScreen
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosScreen
import com.gastosdiarios.gavio.presentation.biometric.BiometricScreen
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationScreen
import com.gastosdiarios.gavio.presentation.configuration.acerca_de.AcercaDeScreen
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.ActualizarMaximoFechaScreen
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesScreen
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.CategoryScreen
import com.gastosdiarios.gavio.presentation.configuration.components.CongratulationsScreen
import com.gastosdiarios.gavio.presentation.configuration.exportar_datos.ExportarDatosScreen
import com.gastosdiarios.gavio.presentation.configuration.notifications.RecordatorioScreen
import com.gastosdiarios.gavio.presentation.configuration.user_profile.UserProfileScreen
import com.gastosdiarios.gavio.presentation.create_gastos_default.CreateGastosDefaultScreen
import com.gastosdiarios.gavio.presentation.home.HomeScreen
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.presentation.home.components.MyFAB
import com.gastosdiarios.gavio.presentation.home.components.topLevelRoutes
import com.gastosdiarios.gavio.presentation.splash_screen.MySplashScreen
import com.gastosdiarios.gavio.presentation.transaction.TransactionsScreen
import com.gastosdiarios.gavio.presentation.welcome.initial.InitialScreen
import com.gastosdiarios.gavio.presentation.welcome.login.LoginScreen
import com.gastosdiarios.gavio.presentation.welcome.register.RegisterScreen
import com.gastosdiarios.gavio.utils.IsInternetAvailableUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun MyAppContent(
    mainActivity: MainActivity,
    splashScreen: SplashScreen
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var currentRoute by remember { mutableStateOf("HomeScreen") }
    // val isInternet by App.ConnectivityStatus.isConnected
    var isInternet by remember {
        mutableStateOf(
            IsInternetAvailableUtils.isInternetAvailable(
                mainActivity
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    if (isLoading) {
        CommonsLoadingScreen(Modifier.fillMaxSize())
    } else if (isInternet) {

        //OTRAS RUTAS PRINCIPALES
        NavHost(
            navController = navController,
            startDestination = SplashScreens
        ) {
            composable<LoginInitScreen> {
                InitialScreen(
                    navigateToRegister = { navController.navigate(RegisterScreen) },
                    navigateToLogin = { navController.navigate(LoginScreen) },
                    navigateToHomeScreen = { navController.navigate(HomeScreen) }
                )
            }
            composable<RegisterScreen> {
                RegisterScreen(
                    onToLogin = { email, password ->
                        //  navController.navigate(Routes.LoginScreen.route + "?email=${email}&password=${password}")
                        navController.navigate(EmailPassword(email, password))
                    }
                )
            }
////            composable<EmailPassword>(
////                Routes.LoginScreen.route + "?email={email}&password={password}",
////                arguments = listOf(
////                    navArgument("email") {
////                        type = NavType.StringType; defaultValue = ""
////                    },
////                    navArgument("password") { type = NavType.StringType; defaultValue = "" }
////                )
////
////            ) { backStackEntry ->
////                val email = requireNotNull(backStackEntry.arguments?.getString("email") ?: "")
////                val password =
////                    requireNotNull(backStackEntry.arguments?.getString("password") ?: "")
////                val args = it.toRoute<EmailPassword>()
////                EmailPassword(email, password)
////                LoginScreen(
////                    email = email,
////                    password = password,
////                    navigateToHome = { navController.navigate(HomeScreen) }
////                )
////            }
            composable<LoginScreen> {
                LoginScreen(
                    email = "",
                    password = "",
                    navigateToHome = { navController.navigate(HomeScreen) }
                )
            }
            composable<EmailPassword> {
                val args = it.toRoute<EmailPassword>()
                EmailPassword(args.email, args.password)
                LoginScreen(
                    email = args.email,
                    password = args.password,
                    navigateToHome = { navController.navigate(HomeScreen) }
                )
            }

            composable<SplashScreens> {
                MySplashScreen(
                    splashScreen = splashScreen,
                    onToHomeScreen = { navController.navigate(HomeScreen) },
                    onToBiometricScreen = { navController.navigate(BiometricScreen) },
                    onToLoginInitScreen = { navController.navigate(LoginInitScreen) }
                )
            }
            composable<HomeScreen> {
                MainScaffold(navController)
            }
            composable<AnalisisGastosScreen> {
                MainScaffold(navController)
            }
            composable<MenuScreen> {
                MainScaffold(navController)
            }

            composable<BiometricScreen> {
                BiometricScreen(mainActivity,
                    onToHomeScreen = { navController.navigate(HomeScreen) }
                )
            }

            composable<TransactionsScreen> {
                TransactionsScreen(
                    //   onBack = { navController.navigate(Routes.HomeScreen.route) }
                    onBack = { navController.popBackStack() }
                )
            }
            composable<UserProfileScreen> {
                UserProfileScreen(
                    onToLoginInitScreen = { navController.navigate(LoginInitScreen) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<CategoryScreen> {
                CategoryScreen(
                    categoryType = CategoryTypeEnum.GASTOS,
                    // onBack = { navController.navigate(Routes.ConfigurationScreen.route) }
                    onBack = { navController.popBackStack() }
                )
            }

            composable<CreateGastosProgramadosScreen> {
                CreateGastosDefaultScreen(onBack = { navController.popBackStack() })
            }

            composable<ActualizarMaximoFechaScreen> {
                ActualizarMaximoFechaScreen(onBack = { navController.popBackStack() })
            }

            composable<AcercaDeScreens> {
                AcercaDeScreen()
            }
            composable<AjustesScreen> {
                AjustesScreen(onBack = { navController.popBackStack() })
            }
            composable<CongratulationsScreen> {
                CongratulationsScreen(
                    onToHomeScreen = { navController.navigate(HomeScreen) }
                )
            }

            composable<RecordatorioScreen> {
                RecordatorioScreen(onBack = { navController.popBackStack() })
            }
            composable<ExportarDatosScreen> {
                ExportarDatosScreen(onBack = { navController.popBackStack() })
            }
        }
    } else {
        ConnectivityErrorScreen(onRetry = {
            isLoading = true
            scope.launch {
                isInternet = IsInternetAvailableUtils.isInternetAvailable(mainActivity)
                delay(5.seconds)
                isLoading = false
            }
        }
        )
    }
}

@Composable
fun MainScaffold(navController: NavHostController) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.homeUiState.collectAsState()
    val isShowSnackbar = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    var selected by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            when (pagerState.currentPage) {
                0 -> { // HomeScreen
                    CommonsToolbar(
                        title = stringResource(id = R.string.toolbar_home),
                        colors = MaterialTheme.colorScheme.surface
                    )
                }

                1 -> { // AnalisisGastosScreen
                    CommonsToolbar(
                        title = stringResource(id = R.string.toolbar_analisis_de_gastos), // Ajusta el título
                        colors = MaterialTheme.colorScheme.surface
                    )
                }

                2 -> { // ConfigurationScreen
                    CommonsToolbar(
                        title = stringResource(id = R.string.toolbar_configuration), // Ajusta el título
                        colors = MaterialTheme.colorScheme.surface
                    )
                }
            }

        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                topLevelRoutes.forEachIndexed { index, top ->
                    selected = pagerState.currentPage == index

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = top.icon),
                                contentDescription = top.name,
                                tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = { Text(text = top.name) },
                        selected = selected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) { // Mostrar FAB solo en HomeScreen (página 0)
                MyFAB(
                    diasRestantes = uiState.diasRestantes,
                    isShowSnackbar,
                    onDismiss = { viewModel.onShowDialogClickTransaction() }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    navigateToMovimientos = { navController.navigate(TransactionsScreen) },
                    onToCreateGastosDefault = { navController.navigate(CreateGastosProgramadosScreen) }
                )

                1 -> AnalisisGastosScreen(modifier = Modifier.padding(innerPadding))

                2 -> ConfigurationScreen(
                    modifier = Modifier.padding(innerPadding),
                    onToHomeScreen = { navController.navigate(HomeScreen) },
                    onToUserProfileScreen = { navController.navigate(UserProfileScreen) },
                    onToCategoriasGastosScreen = { navController.navigate(CategoryScreen) },
                    onToActualizarMaximoFechaScreen = {
                        navController.navigate(ActualizarMaximoFechaScreen)
                    },
                    onToRecordatorioScreen = { navController.navigate(RecordatorioScreen) },
                    onToAcercaDeScreen = { navController.navigate(AcercaDeScreens) },
                    onToAjustesScreen = { navController.navigate(AjustesScreen) },
                    onToExportarDatosScreen = { navController.navigate(ExportarDatosScreen) },
                    onToCongratulationsScreen = { navController.navigate(CongratulationsScreen) }
                )

            }
        }
    }
}