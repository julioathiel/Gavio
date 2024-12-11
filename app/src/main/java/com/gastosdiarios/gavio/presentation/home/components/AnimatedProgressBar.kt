package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = ""
    )
    val progressBarColor = if (animatedProgress >= 0.8f) {
        MaterialTheme.colorScheme.error // Cambia el color a rojo si el progreso es del 80% o más
    } else if (animatedProgress >= 0.7f) {
        MaterialTheme.colorScheme.onSurface //color naranja
    } else {
        MaterialTheme.colorScheme.surface // Mantiene el color por defecto en otro caso
    }
    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
    )
}