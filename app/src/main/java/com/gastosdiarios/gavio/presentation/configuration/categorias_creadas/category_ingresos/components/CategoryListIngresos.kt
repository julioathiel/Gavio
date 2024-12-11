package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.categoriesGastosNuevos

@Composable
fun CategoryListIngresos(
    iconSelected: CategoryCreate?,
    onCategorySelected: (CategoryCreate) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = Modifier
            .heightIn(max = 280.dp) // Ajusta la altura máxima para mostrar solo 5 elementos
    ) {
        items(categoriesGastosNuevos) { category ->
            CategoryItemIngresos(
                category = category,
                isSelected = category == iconSelected,
                onCategorySelected = onCategorySelected
            )
        }
    }
}