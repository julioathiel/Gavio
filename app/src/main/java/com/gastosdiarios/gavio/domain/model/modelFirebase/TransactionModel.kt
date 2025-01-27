package com.gastosdiarios.gavio.domain.model.modelFirebase

import com.gastosdiarios.gavio.domain.enums.TipoTransaccion

data class TransactionModel(
     val uid: String? = null,
     val title: String? = null,
     val subTitle: String? = null,
     val cash: String? = null,
     val tipo: TipoTransaccion? = null,
     val date: String? = null,
     val icon: String? = null,
     val index: Int? = null,
)