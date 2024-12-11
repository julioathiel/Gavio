package com.gastosdiarios.gavio.navigation

import com.gastosdiarios.gavio.data.constants.Constants.ACERCA_DE_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.ACTUALIZAR_MAXIMO_FECHA_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.AJUSTES_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.ANALISIS_GASTOS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.CATEGORIA_GASTOS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.CATEGORIA_INGRESOS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.CONFIGURATION_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.CONGRATULATIONS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.FORGOT_PASSWORD_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.HOME_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.LOADING_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.LOGIN_INIT_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.LOGIN_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.RECORDATORIOS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.REGISTER_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.TRANSACTIONS_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.USER_PROFILE_SCREEN
import com.gastosdiarios.gavio.data.constants.Constants.VIEWPAGER_SCREEN

sealed class Routes(val route: String) {
    data object LoginInitScreen:Routes(LOGIN_INIT_SCREEN)
    data object LoginScreen : Routes(LOGIN_SCREEN)
    data object RegisterScreen : Routes(REGISTER_SCREEN)
    data object HomeScreen : Routes(HOME_SCREEN)
    data object ForgotPasswordScreen : Routes(FORGOT_PASSWORD_SCREEN)
    data object TransactionsScreen : Routes(TRANSACTIONS_SCREEN)
    data object ConfigurationScreen : Routes(CONFIGURATION_SCREEN)
    data object AnalisisGastosScreen : Routes(ANALISIS_GASTOS_SCREEN)
    data object RecordatorioScreen : Routes(RECORDATORIOS_SCREEN)
    data object CategoriaGastosScreen : Routes(CATEGORIA_GASTOS_SCREEN)
    data object CategoriaIngresosScreen : Routes(CATEGORIA_INGRESOS_SCREEN)
    data object ActualizarMaximoFechaScreen : Routes(ACTUALIZAR_MAXIMO_FECHA_SCREEN)
    data object LoadingScreen : Routes(LOADING_SCREEN)
    data object AcercaDeScreen : Routes(ACERCA_DE_SCREEN)
    data object AjustesScreen : Routes(AJUSTES_SCREEN)
    data object CongratulationsScreen : Routes(CONGRATULATIONS_SCREEN)
    data object UserProfileScreen:Routes(USER_PROFILE_SCREEN)
}