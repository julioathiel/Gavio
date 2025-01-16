package com.gastosdiarios.gavio.domain.model.modelFirebase

import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum

data class GastosProgramadosModel(
    val uid: String? = null,
    val title: String? = null,
    val subTitle: String? = null,
    val cash: String? = null,
    val select: Boolean? = null,
    val date: String? = null,
    val icon: String? = null,
    val index: Int? = null,
    val categoryType: CategoryTypeEnum? = null
)