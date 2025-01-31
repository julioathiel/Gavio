package com.gastosdiarios.gavio.domain.model

data class NotificationProgrammedModel(
    val id: Int,
    val smallIcon: Int,
    val title: String,
    val text: String,
    val largeIconResId: Int,
    val priority: Int
)