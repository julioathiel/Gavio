package com.gastosdiarios.gavio.data.domain.model.modelFirebase

import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion

data class TransactionModel(
    val uid: String? = null,
    val title: String? = null,
    val subTitle: String? = null,
    val cash: String? = null,
    val tipoTransaccion: TipoTransaccion? = null,
    val date: String? = null,
    val icon: Int? = null,
    val index: Int? = null,
)