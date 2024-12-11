package com.gastosdiarios.gavio.domain.model

data class UserCreateCategoriesModel(
    val id: Int = System.currentTimeMillis().hashCode(),
    var nameCategory: String? = null,
    var categoryIcon: String? = null
)