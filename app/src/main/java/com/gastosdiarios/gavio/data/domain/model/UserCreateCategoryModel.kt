package com.gastosdiarios.gavio.data.domain.model

import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion

data class UserCreateCategoryModel(
    val uid: String? = null,
    var categoryName: String? = null,
    var categoryIcon: String? = null,
    val categoryType: TipoTransaccion? = null
)