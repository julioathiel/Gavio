package com.gastosdiarios.gavio.domain.model

import com.gastosdiarios.gavio.domain.enums.TipoTransaccion

data class UserCreateCategoryModel(
    val uid: String? = null,
    var categoryName: String? = null,
    var categoryIcon: String? = null,
    val categoryType: TipoTransaccion? = null
)