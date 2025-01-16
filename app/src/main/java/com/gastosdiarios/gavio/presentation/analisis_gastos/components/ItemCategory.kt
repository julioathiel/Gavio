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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosPorCategoriaModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.MathUtils


@Composable
fun ItemCategory(
    modifier: Modifier = Modifier,
    uiState: GastosPorCategoriaModel,
    uiStateList: List<GastosPorCategoriaModel>,
    viewModel: AnalisisGastosViewModel,
    tertiaryContainer: Color,
    onTertiary: Color,
) {
    var expanded: Boolean by remember { mutableStateOf(false) }
    val totalGastado: String = CurrencyUtils.formattedCurrency(uiState.totalGastado ?: 0.0)
    val progressMaximoTotal: Double? by viewModel.totalIngresosRegister.collectAsState()

    val movimientoCategoria: GastosPorCategoriaModel? = when (uiStateList.size) {
        uiStateList.size -> uiStateList.find { it.title == uiState.title }
        else -> null
    }

    val dineroGastadoTotal: Double? = movimientoCategoria?.totalGastado
    val progressTotalGastosActual: Double = dineroGastadoTotal!!.toDouble()

    val progresoRelativo =
        MathUtils.calcularProgresoRelativo(progressMaximoTotal, progressTotalGastosActual)
    val porcentaje = MathUtils.formattedPorcentaje(progresoRelativo)

    Card(
        modifier = modifier.clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = tertiaryContainer)
    ) {
        Column(modifier = modifier) {
            if (expanded) {
                Box(contentAlignment = Alignment.Center,
                    content = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                                AnimatedProgressBarRegistro(
                                    progress = progresoRelativo,
                                    modifier = Modifier.size(50.dp),
                                    cardColor = Color.Transparent,
                                    onCardColor = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    text = "$porcentaje%",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }

                            ProfileIcon(
                                drawableResource = uiState.icon!!.toInt(),
                                description = "",
                                sizeBox = 40,
                                colorCircle = tertiaryContainer,
                                colorIcon = onTertiary,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
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
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                )
                ProfileIcon(
                    drawableResource = uiState.icon!!.toInt(),
                    description = "",
                    sizeBox = 40,
                    colorCircle = tertiaryContainer,
                    colorIcon = onTertiary,
                    modifier = Modifier.padding(8.dp)
                )
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