package com.gastosdiarios.gavio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.credentials.CredentialManager
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.navigation.AppViewModels
import com.gastosdiarios.gavio.navigation.MyAppContent
import com.gastosdiarios.gavio.navigation.Routes
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationViewModel
import com.gastosdiarios.gavio.presentation.configuration.acerca_de.AcercaDeViewModel
import com.gastosdiarios.gavio.presentation.configuration.actualizarMaximoFecha.ActualizarMaximoFechaViewModel
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesViewModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_gastos.CategoryGastosViewModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.CategoryIngresosViewModel
import com.gastosdiarios.gavio.presentation.configuration.user_profile.UserProfileViewModel
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.presentation.home.components.MyAppNavigationActions
import com.gastosdiarios.gavio.presentation.transaction.TransactionsViewModel
import com.gastosdiarios.gavio.presentation.welcome.initial.InitialViewModel
import com.gastosdiarios.gavio.presentation.welcome.login.LoginViewModel
import com.gastosdiarios.gavio.presentation.welcome.register.RegisterViewModel
import com.gastosdiarios.gavio.ui.theme.GavioTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private val initialViewModel: InitialViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val transactionsViewModel: TransactionsViewModel by viewModels()
    private val configurationViewModel: ConfigurationViewModel by viewModels()
    private val userProfileViewModel: UserProfileViewModel by viewModels()
    private val actualizarMaximoFechaViewModel: ActualizarMaximoFechaViewModel by viewModels()
    private val ajustesViewModel: AjustesViewModel by viewModels()
    private val categoryIngresosViewModel: CategoryIngresosViewModel by viewModels()
    private val categoryGastosViewModel: CategoryGastosViewModel by viewModels()
    private val analisisGastosViewModel: AnalisisGastosViewModel by viewModels()
    private val acercaDeViewModel: AcercaDeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        credentialManager = CredentialManager.create(this)

        val appViewModels = AppViewModels(
            initialViewModel,
            loginViewModel,
            registerViewModel,
            homeViewModel,
            transactionsViewModel,
            configurationViewModel,
            userProfileViewModel,
            actualizarMaximoFechaViewModel,
            ajustesViewModel,
            categoryIngresosViewModel,
            categoryGastosViewModel,
            analisisGastosViewModel,
            acercaDeViewModel
        )
        enableEdgeToEdge()
        setContent {
            val darkTheme by ajustesViewModel.selectedMode
            val mode = when (darkTheme) {
                ModeDarkThemeEnum.MODE_AUTO -> isSystemInDarkTheme()
                ModeDarkThemeEnum.MODE_DAY ->  false
                ModeDarkThemeEnum.MODE_NIGHT -> true
            }

            val navController = rememberNavController()
            val navigateAction = remember(navController) { MyAppNavigationActions(navController) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val selectedDestination = navBackStackEntry?.destination?.route ?: Routes.HomeScreen.route

            GavioTheme(darkTheme = mode) {
                MyAppContent(
                    auth = auth,
                    credentialManager = credentialManager,
                    navController = navController,
                    appViewModels = appViewModels,
                    selectedDestination = selectedDestination,
                    navigateTopLevelDestination = navigateAction::navigateTo
                )
            }
        }
    }
}