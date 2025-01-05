package com.gastosdiarios.gavio.presentation.configuration.exportar_datos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gastosdiarios.gavio.data.commons.CommonsIsEmpty
import com.gastosdiarios.gavio.data.commons.ScreenInConstruction
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack

@Composable
fun ExportarDatosScreen(onBack: () -> Unit) {
    Scaffold(topBar = {
        TopAppBarOnBack(
            title = "Exportar datos",
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            onBack = { onBack() }
        )
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
        ScreenInConstruction()
    }
}