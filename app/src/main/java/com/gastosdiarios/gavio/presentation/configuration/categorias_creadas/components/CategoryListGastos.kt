package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.domain.model.CategoryCreate
import com.gastosdiarios.gavio.domain.model.categoriesGastosNuevos

@Composable
fun CategoryListGastos(
    iconSelected: CategoryCreate?,
    onCategorySelected: (CategoryCreate) -> Unit
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(5),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        modifier = Modifier
            .heightIn(max = 280.dp) // Ajusta la altura máxima para mostrar solo 5 elementos
    ) {
        //categorieGastosNuevos es la lista predeterminada
        items(categoriesGastosNuevos) { category ->
            CategoryItemGastos(
                category = category,
                isSelected = category == iconSelected,
                onCategorySelected = onCategorySelected//enviando icono seleccionado
            )
        }
    }
}