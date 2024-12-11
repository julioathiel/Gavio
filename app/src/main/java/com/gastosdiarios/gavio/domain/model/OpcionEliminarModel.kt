package com.gastosdiarios.gavio.domain.model

data class OpcionEliminarModel(val nombre: String, val action: () -> Unit)