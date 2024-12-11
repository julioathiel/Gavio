package com.gastosdiarios.gavio.domain.model

import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum

data class CategoryDefaultModel(
    val onDismiss: Boolean = false,
    val isActivated: Boolean = false,
    val isSelectedEditItem: Boolean = false,
    val titleBottomSheet: String = "",
    val uid: String = "",
    val categoryType: CategoryTypeEnum? = null,
    val selectedCategory: CategoryCreate? = null
)