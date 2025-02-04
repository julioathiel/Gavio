package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.data.domain.model.CategoryCreate

@Composable
fun CategoryItemGastos(
    category: com.gastosdiarios.gavio.data.domain.model.CategoryCreate,
    isSelected: Boolean,
    onCategorySelected: (com.gastosdiarios.gavio.data.domain.model.CategoryCreate) -> Unit
) {
    val iconColor =
        if (isSelected) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onCategorySelected(category) }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = category.icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}