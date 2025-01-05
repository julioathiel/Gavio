package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@Composable
fun ItemCategoriaConMasGastos(
    uiStateList: List<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel
) {
    val porcentaje: Int? by viewModel.porcentajeGasto.collectAsState()
    val icon by viewModel.myIcon.collectAsState()
    // obteniendo el maximo total  gastado
    val category: GastosPorCategoriaModel = uiStateList.maxBy { it.totalGastado ?: 0.0 }
    val form = category.totalGastado ?: 0.0

    //muestra al usuario el total como $ 30.000,00
    val totalGastado = CurrencyUtils.formattedCurrency(form)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileIcon(
                    drawableResource = icon?.toInt() ?: R.drawable.ic_info,
                    description = "icon con mas gastos",
                    sizeBox = 30,
                    colorCircle = Color.Transparent,
                    colorIcon = MaterialTheme.colorScheme.primary
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = category.title ?: "Sin categoría",
                       // color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = totalGastado,
                     //   color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Text(
                    text = "$porcentaje%",
                   // color = MaterialTheme.colorScheme.onTertiaryContainer
                )
//                IconButton(
//                    onClick = { /*Click Implementation*/ },
//                    modifier = Modifier
//                        .clip(CircleShape)
//
//                ) {
////                    Icon(
////                        imageVector = Icons.Default.StarBorder,
////                        contentDescription = stringResource(id = R.string.description_favorite),
////                    )
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_corazon),
//                        contentDescription = "corazon"
//                    )
//
//                }
            }
        }
    }

}