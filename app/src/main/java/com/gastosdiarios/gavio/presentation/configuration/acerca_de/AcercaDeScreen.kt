package com.gastosdiarios.gavio.presentation.configuration.acerca_de

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun AcercaDeScreen(viewModel: AcercaDeViewModel = hiltViewModel()) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        content = {
            Content(viewModel, paddingValues = it)
        }
    )
}

@Composable
fun Content(viewModel: AcercaDeViewModel, paddingValues: PaddingValues) {
    val versionName by viewModel.versionName
    val appIcon by viewModel.appIcon
    val title by viewModel.title
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberAsyncImagePainter(model = appIcon),
            contentDescription = "App Icon",
            modifier = Modifier.size(60.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Versión: $versionName",
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { /* Acción al hacer clic */ }) {
            Text(text = "Ver cambios y novedades de la última versión")
        }
        // Otros elementos como créditos, contactos, políticas de privacidad, etc.
    }
}
