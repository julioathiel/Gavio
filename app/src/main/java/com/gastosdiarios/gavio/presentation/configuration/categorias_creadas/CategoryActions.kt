package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas

import com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel

interface CategoryActions {
    fun onEditClick(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel, iconSelect :Int)
    fun onDeleteClick(item: com.gastosdiarios.gavio.data.domain.model.UserCreateCategoryModel)
}