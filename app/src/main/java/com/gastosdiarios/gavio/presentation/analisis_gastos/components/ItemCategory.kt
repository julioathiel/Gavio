package com.gastosdiarios.gavio.presentation.analisis_gastos.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.MathUtils

@Composable
fun ItemCategory(
    uiState: GastosPorCategoriaModel,
    uiStateList: List<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel,
    tertiaryContainer: Color,
    onTertiary: Color,
) {
    var expanded by remember { mutableStateOf(false) }
    val totalGastado = CurrencyUtils.formattedCurrency(uiState.totalGastado ?: 0.0)
    val progressMaximoTotal: Double? by viewModel.totalIngresosRegister.collectAsState()

    val movimientoCategoria = when (uiStateList.size) {
        uiStateList.size -> uiStateList.find { it.title == uiState.title }
        else -> null
    }

    val dineroGastadoTotal = movimientoCategoria?.totalGastado
    // Obtén el valor actual de los gastos para el ProgressBar, por ejemplo, del uiState
    val progressTotalGastosActual: Double = dineroGastadoTotal!!.toDouble()

    val progresoRelativo =
        MathUtils.calcularProgresoRelativo(progressMaximoTotal, progressTotalGastosActual)
    val porcentaje = MathUtils.formattedPorcentaje(progresoRelativo)

    Card(colors = CardDefaults.cardColors(containerColor = tertiaryContainer)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { expanded = !expanded }
                .background(tertiaryContainer),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (expanded) {
                    Column {
                        Box(
                            Modifier.padding(top = 16.dp, start = 16.dp),
                            contentAlignment = Alignment.Center, content = {
                                AnimatedProgressBarRegistro(
                                    progress = progresoRelativo,
                                    cardColor = Color.Transparent,
                                    onCardColor = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = "$porcentaje%",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        )

                        Text(
                            text = uiState.title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = if (expanded) Int.MAX_VALUE else 1,
                            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                        Text(
                            text = totalGastado,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                        )
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!expanded) {
                    Text(
                        text = uiState.title ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_medium)).weight(1f)
                    )
                    ProfileIcon(
                        drawableResource = uiState.icon!!.toInt(),
                        description = "",
                        sizeBox = 40,
                        colorBackgroundBox = Color.Transparent,
                        colorIcon = onTertiary,
                        modifier = Modifier.padding( end = 8.dp, top = 8.dp, bottom = 8.dp)
                    )
                }else{
                    Spacer(modifier = Modifier.weight(1f))
                    ProfileIcon(
                        drawableResource = uiState.icon!!.toInt(),
                        description = "",
                        sizeBox = 40,
                        colorBackgroundBox = Color.Transparent,
                        colorIcon = onTertiary,
                        modifier = Modifier.padding( end = 8.dp, top = 8.dp)
                    )
                }

            }

        }
    }
}

@Composable
fun AnimatedProgressBarRegistro(
    progress: Float,
    modifier: Modifier = Modifier,
    cardColor: Color,
    onCardColor: Color
) {

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000), label = ""
    )

    CircularProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier,
        color = onCardColor,
        // trackColor = cardColor
    )
}