package com.gastosdiarios.gavio.domain.model

import com.gastosdiarios.gavio.domain.enums.TipoTransaccion

data class CategoryDefaultModel(
    val uid: String = "",
    val titleBottomSheet: String = "",
    val onDismiss: Boolean = false,
    val isActivated: Boolean = false,
    val isSelectedEditItem: Boolean = false,
    val errorConectionInternet: Boolean = false,
    val categoryType: TipoTransaccion? = null,
    val selectedCategory: CategoryCreate? = null,
)