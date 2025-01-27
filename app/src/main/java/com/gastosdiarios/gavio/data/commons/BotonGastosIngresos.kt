package com.gastosdiarios.gavio.data.commons

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
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion

@Composable
fun BotonGastosIngresos(
    modifier: Modifier = Modifier,
    enabledBotonGastos: Boolean,
    botonActivado: Int,
    onTipoSeleccionado: (TipoTransaccion) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(botonActivado) }
    val options = listOf(TipoTransaccion.GASTOS, TipoTransaccion.INGRESOS)
    SingleChoiceSegmentedButtonRow (modifier = modifier){
        options.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val color = if (isSelected) {
                when (label) {
                    TipoTransaccion.GASTOS -> Color(0xFFFFEBEE)
                    TipoTransaccion.INGRESOS -> colorResource(id = R.color.fondoVerdeDinero)
                }
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            val colorText = if (isSelected) {
                when (label) {
                    TipoTransaccion.GASTOS -> colorResource(id = R.color.rojoDinero)
                    TipoTransaccion.INGRESOS -> colorResource(id = R.color.verdeDinero)
                }
            } else {
                Color.Transparent //los bordes no se veran
            }

            val enabledButton = enabledBotonGastos || label != TipoTransaccion.GASTOS

            SegmentedButton(
                modifier = modifier,
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                onClick = {
                    selectedIndex = index
                    onTipoSeleccionado(options[index])
                },
                selected = index == selectedIndex,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = color,
                    activeContentColor = colorText,
                  //  inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant

                    disabledInactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,


                ),
                border = BorderStroke(color = colorText, width = 0.dp),
                // Deshabilitar el botón de gastos si enabledBotonGastos es falso o habilitar todoo si esta en true
                enabled = enabledButton
            ) {
                Text(
                    label.toString(),
                    color = if (isSelected) colorText else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}