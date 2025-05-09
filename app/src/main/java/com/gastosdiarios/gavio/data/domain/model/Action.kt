package com.gastosdiarios.gavio.data.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class Action(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)