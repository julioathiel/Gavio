package com.gastosdiarios.gavio.domain.model.modelFirebase

import java.time.LocalDate

data class TransactionModel(
     val uid: String? = null,
     val title: String? = null,
     val subTitle: String? = null,
     val cash: String? = null,
     val select: Boolean? = null,
     val date: String? = null,
     val icon: String? = null,
     val index: Int? = null,
)