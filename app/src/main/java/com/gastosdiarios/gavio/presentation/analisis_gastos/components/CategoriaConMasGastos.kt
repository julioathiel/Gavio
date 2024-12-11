package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@Composable
fun CategoriaConMasGastos(
    uiStateList: List<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel
) {
    val porcentaje: Int? by viewModel.porcentajeGasto.collectAsState()
    // obteniendo el maximo total  gastado
    val category: GastosPorCategoriaModel = uiStateList.maxBy { it.totalGastado ?: 0.0 }
    val form = category.totalGastado ?: 0.0

    //muestra al usuario el total como $ 30.000,00
    val totalGastado = CurrencyUtils.formattedCurrency(form)


    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.lo_mas_gastado_este_mes),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 10.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {

                Text(
                    text = category.title ?: "Sin categoría",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = totalGastado,
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Text(
            text = "$porcentaje%",
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}