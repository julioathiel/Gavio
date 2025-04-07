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