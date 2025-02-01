package com.gastosdiarios.gavio.main_scaffold.data_class

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

data class Screen(
    val nameToolbar: String, // Nombre de la pantalla (para la barra superior)
    val name :String, // Nombre que acompaña al icono de la barra inferior
    val route: String, // Ruta de navegación de la pantalla
    @DrawableRes val icon: Int, // ID del icono de la pantalla (para la barra inferior)
    val content: @Composable (PaddingValues, SnackbarHostState) -> Unit // Composable que representa el contenido de la pantalla
)