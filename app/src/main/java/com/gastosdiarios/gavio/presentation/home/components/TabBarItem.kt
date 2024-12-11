package com.gastosdiarios.gavio.presentation.home.components

import androidx.annotation.DrawableRes
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.navigation.Routes


class MyAppNavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: MyAppTopLevelDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            //evita la misma copia del mismo destino
            launchSingleTop = true
        }
    }
}

data class MyAppTopLevelDestination(
    val route: String,
    @DrawableRes val selectedIcon: Int,
    val iconTextId: Int
)

val TOP_LEVEL_DESTINATIONS = listOf(
    MyAppTopLevelDestination(
        route = Routes.HomeScreen.route,
        selectedIcon = R.drawable.ic_home_filled,
        iconTextId = R.string.home
    ),
    MyAppTopLevelDestination(
        route = Routes.AnalisisGastosScreen.route,
        selectedIcon = R.drawable.ic_barra_filled,
        iconTextId = R.string.analisis
    ),
    MyAppTopLevelDestination(
        route = Routes.ConfigurationScreen.route,
        selectedIcon = R.drawable.ic_menu,
        iconTextId = R.string.menu
    )
)