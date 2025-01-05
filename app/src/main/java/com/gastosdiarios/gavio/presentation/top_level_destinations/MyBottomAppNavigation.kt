package com.gastosdiarios.gavio.presentation.top_level_destinations

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.presentation.home.components.topLevelRoutes

@Composable
fun MyBottomAppNavigation(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        topLevelRoutes.forEach { topLevelRoute ->
            val selected = currentDestination?.hierarchy?.any { it.route?.contains(topLevelRoute.name, ignoreCase = true) == true } == true
            Log.d("selected", selected.toString())

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = topLevelRoute.icon),
                        contentDescription = topLevelRoute.name,
                        tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text(text = topLevelRoute.name) },
                selected = currentDestination?.hierarchy?.any { it.route == topLevelRoute.route } == true,
                onClick = {
                    navController.navigate(topLevelRoute.route) {
                        // Aparece el destino inicial del gráfico para
                        // evitar la creación de una gran pila de destinos
                        // en la pila de tareas a medida que los usuarios seleccionan elementos
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Evite múltiples copias del mismo destino al
                        // volver a seleccionar el mismo elemento
                        launchSingleTop = true
                        // Restaurar el estado al volver a seleccionar un elemento seleccionado previamente
                        restoreState = true
                    }
                },
            )
        }
    }
}