package com.gastosdiarios.gavio.presentation.configuration.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ShareSheetButton(appDownloadLink: String, onDissmiss: () -> Unit) {
    val shareApp: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    LaunchedEffect(Unit) {
        // Lógica para compartir la app
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "¡Descarga esta increíble app! : $appDownloadLink")
        }

        shareApp.launch(Intent.createChooser(intent, "Compartir la app a través de:"))
        onDissmiss() // Llama a la función onDismiss después de lanzar la actividad de compartir
    }
}