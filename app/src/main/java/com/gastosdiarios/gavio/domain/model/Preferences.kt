package com.gastosdiarios.gavio.domain.model

data class Preferences(
    val securityActivated: Boolean? = null,
    val numeroGuardado: String? = null,
    val switchActivado: Boolean? = null,
    val hour: Int? = null,
    val minute: Int? = null
)
