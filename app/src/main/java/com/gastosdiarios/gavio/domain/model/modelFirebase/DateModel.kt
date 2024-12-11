package com.gastosdiarios.gavio.domain.model.modelFirebase

data class DateModel(
    val userId: String? = null,
    var date: String? = null,
    @field:JvmField
    var isSelected: Boolean = true
)