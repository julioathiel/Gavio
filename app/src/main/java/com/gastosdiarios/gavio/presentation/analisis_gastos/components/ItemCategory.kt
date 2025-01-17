package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.MathUtils


@Composable
fun ItemCategory(
    uiState: GastosPorCategoriaModel,
    uiStateList: List<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel
) {
    val totalGastado = CurrencyUtils.formattedCurrency(uiState.totalGastado ?: 0.0)

    val progressMaximoTotal:Double? by viewModel.totalIngresosRegister.collectAsState()

    val movimientoCategoria = when (uiStateList.size) {
        uiStateList.size -> uiStateList.find{ it.title == uiState.title  }
        else -> null
    }

    val dineroGastadoTotal = movimientoCategoria?.totalGastado
    // Obtén el valor actual de los gastos para el ProgressBar, por ejemplo, del uiState
    val progressTotalGastosActual: Double = dineroGastadoTotal!!.toDouble()

    val progresoRelativo = MathUtils.calcularProgresoRelativo(progressMaximoTotal, progressTotalGastosActual)
    val porcentaje = MathUtils.formattedPorcentaje(progresoRelativo)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // icono de la categoria
        Box(
            Modifier
                .clip(shape = CircleShape)
                .size(48.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = uiState.icon!!.toInt()),
                contentDescription = "",
                alignment = Alignment.Center,
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        //contenido principal
        Column {
            //nombre de la categoria
            Text(
                text = uiState.title ?: "",
                fontSize = 16.sp
            )
            //cantidad gastado de la categoria
            Row {

                Text(
                    text = totalGastado,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$porcentaje%",
                    fontSize = 14.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AnimatedProgressBarRegistro(
                progress = progresoRelativo,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AnimatedProgressBarRegistro(
    progress: Float,
    modifier: Modifier = Modifier
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier,
    )
}