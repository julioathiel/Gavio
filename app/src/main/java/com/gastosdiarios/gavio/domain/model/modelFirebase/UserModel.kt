package com.gastosdiarios.gavio.domain.model.modelFirebase

data class UserModel(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val photoUrl: String? = null,
    val date: String? = null,
    val provider: String? = null
)