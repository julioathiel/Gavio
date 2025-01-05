package com.gastosdiarios.gavio.presentation.home.components

import androidx.annotation.DrawableRes
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.navigation.AnalisisGastosScreen
import com.gastosdiarios.gavio.navigation.HomeScreen
import com.gastosdiarios.gavio.navigation.MenuScreen

data class TopLevelRoute<T:Any>(val name: String, val route: T, @DrawableRes val icon: Int)

val topLevelRoutes = listOf(
    TopLevelRoute(name = "Home", route = HomeScreen, icon = R.drawable.ic_home_filled),
    TopLevelRoute(name = "Analisis", route = AnalisisGastosScreen, icon =  R.drawable.ic_barra_filled),
    TopLevelRoute(name = "Menu", route = MenuScreen, icon = R.drawable.ic_menu),
)