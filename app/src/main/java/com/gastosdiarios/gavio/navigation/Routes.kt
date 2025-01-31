package com.gastosdiarios.gavio.navigation

import kotlinx.serialization.Serializable

@Serializable object SplashScreens
@Serializable object LoginInitScreen
@Serializable
object LoginScreen
@Serializable
object RegisterScreen
@Serializable
object ForgotPasswordScreen
@Serializable
object BiometricScreen
@Serializable
object HomeScreen
@Serializable
object TransactionsScreen
@Serializable
object MenuScreen
@Serializable
object AnalisisGastosScreen
@Serializable
object NotificationsScreen
@Serializable
object CategoryScreen
@Serializable
object CreateGastosProgramadosScreen
@Serializable
object ActualizarMaximoFechaScreen
@Serializable
object AcercaDeScreens
@Serializable
object AjustesScreen
@Serializable
object ExportarDatosScreen
@Serializable
object CongratulationsScreen
@Serializable
object UserProfileScreen

@Serializable
data class EmailPassword(val email: String, val password: String)

sealed class NavigationAction {
    data object  ToPantallaUno : NavigationAction()
    data object  ToPantallaDos : NavigationAction()
    data object ToSplash : NavigationAction()
    data object ToLoginInit : NavigationAction()
    data object ToLogin : NavigationAction()
    data object ToRegister : NavigationAction()
    data object ToForgotPassword : NavigationAction()
    data object ToBiometric : NavigationAction()
    data object ToHome : NavigationAction()
    data object ToTransactions : NavigationAction()
    data object ToMenu : NavigationAction()
    data object ToAnalisisGastos : NavigationAction()
    data object ToRecordatorio : NavigationAction()
    data object ToCategory : NavigationAction()
    data object ToCreateGastosProgramados : NavigationAction()
    data object ToActualizarMaximoFecha : NavigationAction()
    data object ToAcercaDe : NavigationAction()
    data object ToAjustes : NavigationAction()
    data object ToExportarDatos : NavigationAction()
    data object ToCongratulations : NavigationAction()
    data object ToUserProfile : NavigationAction()
}