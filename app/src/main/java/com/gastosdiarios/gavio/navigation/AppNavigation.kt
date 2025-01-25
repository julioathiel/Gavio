package com.gastosdiarios.gavio.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.gastosdiarios.gavio.MainActivity
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.ConnectivityErrorScreen
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.main_scaffold.scaffold.MainScaffold
import com.gastosdiarios.gavio.presentation.biometric.BiometricScreen
import com.gastosdiarios.gavio.presentation.configuration.acerca_de.AcercaDeScreen
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.ActualizarMaximoFechaScreen
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesScreen
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.CategoryScreen
import com.gastosdiarios.gavio.presentation.configuration.components.CongratulationsScreen
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosProgramadosScreen
import com.gastosdiarios.gavio.presentation.configuration.exportar_datos.ExportarDatosScreen
import com.gastosdiarios.gavio.presentation.configuration.notifications.RecordatorioScreen
import com.gastosdiarios.gavio.presentation.configuration.user_profile.UserProfileScreen
import com.gastosdiarios.gavio.presentation.splash_screen.MySplashScreen
import com.gastosdiarios.gavio.presentation.transaction.TransactionsScreen
import com.gastosdiarios.gavio.presentation.welcome.initial.InitialScreen
import com.gastosdiarios.gavio.presentation.welcome.login.LoginScreen
import com.gastosdiarios.gavio.presentation.welcome.register.RegisterScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun MyAppContent(
    mainActivity: MainActivity,
    splashScreen: SplashScreen,
    viewModel: MyAppContentViewmodel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isInternet by viewModel.isInternetAvailable.collectAsState() // Observa desde el ViewModel
    val isLoading by viewModel.isLoading.collectAsState() // Observa el estado de carga desde el ViewModel


    val scope = rememberCoroutineScope()
    if (isLoading) {
        CommonsLoadingScreen(Modifier.fillMaxSize())
    } else if (isInternet) {

//        LaunchedEffect(Unit) {
//            viewModel.navigationAction.collect { action ->
//                when (action) {
//                    NavigationAction.ToSplash -> navController.navigate(SplashScreens)
//                    NavigationAction.ToLoginInit -> navController.navigate(LoginInitScreen)
//                    NavigationAction.ToLogin -> navController.navigate(LoginScreen)
//                    NavigationAction.ToForgotPassword -> navController.navigate(ForgotPasswordScreen)
//                    NavigationAction.ToRegister -> navController.navigate(RegisterScreen)
//                    NavigationAction.ToHome -> navController.navigate(HomeScreen)
//                    NavigationAction.ToAcercaDe -> navController.navigate(AcercaDeScreens)
//                    NavigationAction.ToActualizarMaximoFecha -> navController.navigate(ActualizarMaximoFechaScreen)
//                    NavigationAction.ToAjustes -> navController.navigate(AjustesScreen)
//                    NavigationAction.ToAnalisisGastos -> navController.navigate(AnalisisGastosScreen)
//                    NavigationAction.ToBiometric -> navController.navigate(BiometricScreen)
//                    NavigationAction.ToCategory -> navController.navigate(CategoryScreen)
//                    NavigationAction.ToCongratulations -> navController.navigate(CongratulationsScreen)
//                    NavigationAction.ToCreateGastosProgramados -> navController.navigate(CreateGastosProgramadosScreen)
//                    NavigationAction.ToExportarDatos -> navController.navigate(ExportarDatosScreen)
//                    NavigationAction.ToMenu -> navController.navigate(MenuScreen)
//                    NavigationAction.ToRecordatorio -> navController.navigate(RecordatorioScreen)
//                    NavigationAction.ToTransactions -> navController.navigate(TransactionsScreen)
//                    NavigationAction.ToUserProfile -> navController.navigate(UserProfileScreen)
//                    NavigationAction.ToPantallaDos -> navController.navigate(PantallasDos.route)
//                    NavigationAction.ToPantallaUno -> navController.navigate(PantallasUno.route)
//                    null -> {}
//                }
//                viewModel.resetNavigationAction()
//            }
//        }

        //OTRAS RUTAS PRINCIPALES
        NavHost(
            navController = navController,
            startDestination = SplashScreens
        ) {
            composable<SplashScreens> {
                MySplashScreen(
                    splashScreen = splashScreen,
                    onToHomeScreen = { navController.navigate(HomeScreen) },
                    onToBiometricScreen = { navController.navigate(BiometricScreen) },
                    onToLoginInitScreen = { navController.navigate(LoginInitScreen) }
                )
            }

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
                        navController.navigate(EmailPassword(email, password))
                    }
                )
            }

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
                CategoryScreen(onBack = { navController.popBackStack() })
            }

            composable<CreateGastosProgramadosScreen> {
                CreateGastosProgramadosScreen(onBack = { navController.popBackStack() })
            }

            composable<ActualizarMaximoFechaScreen> { ActualizarMaximoFechaScreen(onBack = { navController.popBackStack() }) }

            composable<AcercaDeScreens> { AcercaDeScreen() }
            composable<AjustesScreen> { AjustesScreen(onBack = { navController.popBackStack() }) }

            composable<CongratulationsScreen> {
                CongratulationsScreen(onToHomeScreen = { navController.navigate(HomeScreen) })
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
            scope.launch {
                viewModel.checkInternetConnection(context = mainActivity)
                delay(5.seconds)
            }
        }
        )
    }
}

@Composable
fun PantallaDos(viewModel: MyAppContentViewmodel) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Button(onClick = {
            viewModel.navigateToPantallaUno()
        }) {
            Text(text = "Navegar a pantallaUno")
        }
    }
}

@Composable
fun PantallaUno(viewModel: MyAppContentViewmodel) {
Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
    Button(onClick = {
        viewModel.navigateToPantallaDos()
    }) {
        Text(text = "Navegar a pantallaDos")
    }
}
}
