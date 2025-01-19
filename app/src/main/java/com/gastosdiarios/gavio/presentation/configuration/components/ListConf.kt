package com.gastosdiarios.gavio.presentation.configuration.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.domain.enums.ItemConfigurationEnum

@Composable
fun ListConf(
    modifier: Modifier,
    items: List<ItemConfigurationEnum>,
    onItemClick: (ItemConfigurationEnum) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        var lastCategory: String? = null
        items.forEach {
            if (lastCategory != it.category) {
                // Agregar línea divisoria
                if (lastCategory != null) {
                    HorizontalDivider(modifier = Modifier.padding(start = 70.dp), color = MaterialTheme.colorScheme.surfaceContainerHigh)
                }
                // Actualizar la última categoría
                lastCategory = it.category
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(it)
                    }
                    .padding(16.dp),
                verticalAlignment = CenterVertically,

                ) {
                Image(
                    painter = painterResource(id = it.icon),
                    contentDescription = it.title.toString(),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.padding(horizontal = 16.dp))
                Column {
                    Text(
                        text = context.getString(it.title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = context.getString(it.description),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}