package com.gastosdiarios.gavio.domain.model

data class NotificationProgramadaModel(
    val id: Int,
    val smallIcon: Int,
    val title: String,
    val text: String,
    val largeIconResId: Int,
    val priority: Int
)