package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel

interface CategoryActions {
    fun onEditClick(item: UserCreateCategoryModel, iconSelect :Int)
    fun onDeleteClick(item: UserCreateCategoryModel)
}