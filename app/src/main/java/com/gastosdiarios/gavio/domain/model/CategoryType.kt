package com.gastosdiarios.gavio.domain.model

import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum

data class CategoryDefaultModel(
    val uid: String = "",
    val titleBottomSheet: String = "",
    val onDismiss: Boolean = false,
    val isActivated: Boolean = false,
    val isSelectedEditItem: Boolean = false,
    val errorConectionInternet: Boolean = false,
    val categoryType: CategoryTypeEnum? = null,
    val selectedCategory: CategoryCreate? = null,
)