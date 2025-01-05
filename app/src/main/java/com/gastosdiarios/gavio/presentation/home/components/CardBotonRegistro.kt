package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.MathUtils
//CARD DE REGISTRO TOTAL DE INGRESOS Y GASTOS
@Composable
fun CardBotonRegistro(mostrandoDineroTotalIngresos: Double?, mostrandoDineroTotalGastos: Double?) {

    val progressRelative = MathUtils.calcularProgresoRelativo(mostrandoDineroTotalIngresos, mostrandoDineroTotalGastos)
    val porcentaje = MathUtils.formattedPorcentaje(progressRelative)


    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "$porcentaje%",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            AnimatedProgressBar(
                progress = progressRelative,
                modifier = Modifier.fillMaxWidth()
            )
            CommonsSpacer(width = 0.dp, height = 16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {

                Text(
                    text = CurrencyUtils.formattedCurrency(mostrandoDineroTotalGastos),
                    color = colorResource(id = R.color.rojoDinero),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(text = "/", modifier = Modifier.padding(horizontal = 4.dp))
                Text(
                    text = CurrencyUtils.formattedCurrency(mostrandoDineroTotalIngresos),

                    color = colorResource(id = R.color.verdeDinero),
                    style = MaterialTheme.typography.titleMedium
                )

            }
        }
    }
}