package com.gastosdiarios.gavio.domain.model

import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum

data class UserCreateCategoryModel(
    val uid: String? = null,
    var categoryName: String? = null,
    var categoryIcon: String? = null,
    val categoryType: CategoryTypeEnum? = null
)
// Int = System.currentTimeMillis().hashCode(),