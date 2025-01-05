package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum

@Composable
fun BotonGastosIngresosPantallaGastos(modifier: Modifier = Modifier, onTipoSeleccionado: (CategoryTypeEnum) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf(CategoryTypeEnum.GASTOS, CategoryTypeEnum.INGRESOS)
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val color = if (isSelected) {
                when (label) {
                    CategoryTypeEnum.GASTOS -> Color(0xFFFFEBEE)
                    CategoryTypeEnum.INGRESOS -> colorResource(id = R.color.fondoVerdeDinero)
                }
            } else {
                Color.Transparent
            }

            val colorText = if (isSelected) {
                when (label) {
                    CategoryTypeEnum.GASTOS -> colorResource(id = R.color.rojoDinero)
                    CategoryTypeEnum.INGRESOS -> colorResource(id = R.color.verdeDinero)
                }
            } else {
                Color.Transparent
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex = index
                    onTipoSeleccionado(options[index])
                },
                selected = index == selectedIndex,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = color,
                    activeContentColor = colorText,
                    inactiveContainerColor = Color.Transparent
                ),
                border = BorderStroke(color = colorText, width = 1.dp)
            ) {
                Text(
                    label.toString(),
                    color = if (isSelected) colorText else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}