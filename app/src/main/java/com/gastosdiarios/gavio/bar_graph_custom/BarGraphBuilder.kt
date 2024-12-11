package com.gastosdiarios.gavio.bar_graph_custom

import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun <T> BarGraph(config: BarGraphConfig<T>) {
    var selectedBarIndex by remember { mutableIntStateOf(-1) }
    val ejeCartesianoX = 30
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        //gráfico donde muestra el dinero de cada barra al ser seleccionada
        ViewBarText(selectedBarIndex = selectedBarIndex, config = config)

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {

            Spacer(modifier = Modifier.padding(30.dp))
            Box(
                modifier = Modifier.height(config.altura)
            ) {
                GraphEleCartesiano(config = config)
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start =
                        if (config.etiquetaEjeYVisible) {
                            ejeCartesianoX.dp + 10.dp
                        } else {
                            0.dp
                        }
                    )
            ) {
                LazyRowItems(
                    config = config,
                    selectedBarIndex = selectedBarIndex,
                    onBarClick = { index ->
                        selectedBarIndex = if (selectedBarIndex == index) -1 else index
                    }
                )

                HorizontalDivider(
                    Modifier
                        .offset(y = (-30).dp),
                    color = if (config.lineaBaseVisible) config.lineaBaseColor else Color.Transparent
                )
            }
        }
    }
}

@Composable
fun <T> ViewBarText(
    selectedBarIndex: Int,
    config: BarGraphConfig<T>
) {
    val selectedBarValue = if (selectedBarIndex != -1) {
        config.dataToLabel(config.barGraphList[selectedBarIndex])
    } else {
        ""
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.viewBarHeigth)
            .padding(config.viewBarPaddingBottom)
            .background(
                color = config.viewBarContainerColor,
                shape = RoundedCornerShape(config.viewBarCornerRadius)
            ),
        contentAlignment = Alignment.Center
    ) {
        val textDinero = selectedBarValue.toDoubleOrNull()?.let { amount ->
            formattedCurrency(amount)
        } ?: ""
        Text(
            text = textDinero,
            style = TextStyle(color = config.viewBarTextColor, fontSize = config.viewBarTextSize)
        )
    }
}
fun formattedCurrency(amount: Double): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    val formattedAmount = currencyFormat.format(amount)
    val currencySymbol = currencyFormat.currency?.symbol ?: "$"
    return formattedAmount.replace(currencySymbol, "").trim()
}

@Composable
private fun <T> EtiquetaBarGraph(
    data: T,
    config: BarGraphConfig<T>,
) {
    val textBounds = Rect()
    val textPaint = Paint().apply {
        isAntiAlias = true
        textSize = config.etiquetaTextSize.value * 1.5f
        color = config.etiquetaTextColor.toArgb()
        textAlign = Paint.Align.CENTER
    }
    val text = config.dataToValue(data).toInt().toString()
    textPaint.getTextBounds(text, 0, text.length, textBounds)
    val textWidth = textBounds.width().toFloat()
    val textHeight = textBounds.height().toFloat()
    val extra = 60// Ajusta este valor para agregar más espacio interno
    // Calcular el tamaño del contenedor
    val containerWidthFloat = textWidth + extra
    val containerHeightFloat = (textHeight + extra) / 2

    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {

            val textXCenter = size.width / 2f

            // Calcular la posición de la etiqueta
            val containerX: Float =
                textXCenter - (containerWidthFloat / 2)//centra bien el contenedor

            val containerY: Float =
                size.height - (config.dataToValue(data) / config.maxGraphValue) * size.height - containerHeightFloat
            val yOffset = -5f // Ajusta este valor para el desplazamiento deseado
            translate(top = yOffset) {
                // Dibujar el rectángulo contenedor
                drawRoundRect(
                    color = if (config.etiquetaVisible) config.etiquetaContainerColor else Color.Transparent,
                    size = Size(containerWidthFloat, containerHeightFloat),
                    cornerRadius = CornerRadius(config.etiquetaCornerRadius),
                    topLeft = Offset(containerX, containerY)
                )

                // Dibujar el texto de la etiqueta
                if (config.etiquetaEjeYVisible) {
                    drawContext.canvas.nativeCanvas.drawText(
                        text,
                        textXCenter,
                        (containerY + (containerHeightFloat / 2f) + (textHeight / 2f)) - 1,
                        textPaint
                    )
                }
            }
        }
    )
}


@Composable
private fun <T> LazyRowItems(
    config: BarGraphConfig<T>,
    selectedBarIndex: Int,
    onBarClick: (Int) -> Unit
) {
    val state = rememberLazyListState()
    val barShape = when (config.roundType) {
        BarType.CIRCULAR_TYPE -> CircleShape
        BarType.TOP_CURVED -> RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
    }
    // Desplazarse al último elemento al actualizarse
    //esto ayuda a que si no se ve el ultimo mes en pantalla, se pueda visualizar
    LaunchedEffect(state) {
        try {
            if(config.barGraphList.isNotEmpty()){
                delay(3000)
                state.scrollToItem(index = config.barGraphList.size - 1)
            }

        } catch (e: Exception) {
            // Maneja la excepción aquí (puedes registrarla o tomar alguna acción específica)
            Log.e("ScrollToItemError", "Ocurrió una excepción: ${e.message}")
            Log.e("ScrollToItemError", Log.getStackTraceString(e))
            Log.e("ScrollToItemError", "Error al desplazar al último elemento: ${e.message}")
        }
    }

    LazyRow(
        state = state,
        horizontalArrangement = Arrangement.spacedBy(config.dataBarEspacioEntreBarras)
    ) {
        items(config.barGraphList.size) { index ->
            val animationBarra by remember { mutableStateOf(false) }
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

//            LaunchedEffect(key1 = true) {
//                animationBarra = true
//            }
            val data = config.barGraphList[index]
            val value = config.dataToValue(data) //ingresa el valor de porcentaje
            val month = config.dataToMonth(data) //ingresa a los meses de la lista
            var barColor =
                if (index == selectedBarIndex) config.dataBarSelectedBarColor else config.dataBarUnSelectedBarColor

            val graphBarHeight by animateFloatAsState(
                targetValue = if (animationBarra) value / config.maxGraphValue else 0f,
                animationSpec = tween(durationMillis = 1000, delayMillis = 0), label = ""
            )
            val item = value / config.maxGraphValue
            if (month == currentMonth) {
                barColor = config.dataBarSelectedBarColor
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.height(config.altura.plus(30.dp))
            ) {
                // este box es imprescindible para mantener las
                // barras de abajo hacia arriba al presionar y que se vean las etiquetas
                Box(
                    modifier = Modifier.height(config.altura),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    //muestra cada barra del mes
                    Box(
                        modifier = Modifier
                           // .fillMaxHeight(graphBarHeight)
                            .fillMaxHeight(item)
                            .width(config.dataBarWidth)
                            .background(barColor, barShape)
                            .clickable { onBarClick(index) }
                    )
                    if (index == selectedBarIndex) {
                        EtiquetaBarGraph(
                            data = data,
                            config = config
                        )
                    }
                }
                //linea vertical
                Column(
                    Modifier.width(config.dataBarWidth),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                        modifier = Modifier
                            .height(config.lineaVerticalWidth)
                            .width(config.lineaVerticalHeigth)
                            .offset(y = (2).dp)
                            .rotate(90f),
                        color = if (config.lineaVerticalVisible) config.lineaVerticalColor else Color.Transparent
                    )
                    Spacer(modifier = Modifier.padding(vertical = 2.dp))
                    //mostrando meses
                    Text(
                        modifier = Modifier.rotate(config.etiquetaEjeXRotate),
                        text = month,
                        color = config.etiquetaEjeXColor
                    )
                    Spacer(modifier = Modifier.padding(vertical = 30.dp))
                }
            }
        }
    }
}



@Composable
private fun <T> GraphEleCartesiano(
    config: BarGraphConfig<T>
) {
    val density = LocalDensity.current
    // Y-axis scale text paint
    val textPaint = remember(density) {
        Paint().apply {
            color = config.etiquetaEjeYColor.hashCode()
            textAlign = Paint.Align.RIGHT
            textSize = density.run { 12.sp.toPx() }
        }
    }

    // Calculamos la separación entre las líneas
    val separacion =
        (config.maxGraphValue - config.minGraphValue) / (config.lineasEjeGraphcantidadLineas)

    Row {
        // Y-Axis Scale Text
        if (config.etiquetaEjeYVisible) {
            Column {
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 10.dp, end = 30.dp)
                ) {
                    val stepHeight =
                        size.height / (config.lineasEjeGraphcantidadLineas) // Altura entre cada línea

                    for (i in 0 until config.lineasEjeGraphcantidadLineas.plus(1)) {
                        val value = config.maxGraphValue - (i * separacion)
                        val text = value.toString()
                        val x = 40f
                        // Calculamos la posición y tomando en cuenta el tamaño del texto
                        val y =
                            i * stepHeight + textPaint.fontMetrics.descent + (stepHeight * 0.02f)
                        drawContext.canvas.nativeCanvas.drawText(text, x, y, textPaint)
                    }
                }
            }
        }
        // Línea divisoria

        Column(
            Modifier.height(config.altura),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            var sum = 0
            repeat(config.lineasEjeGraphcantidadLineas.plus(1)) {
                sum++
                val color = if (config.lineasEjeGraphVisible) {
                    if (sum < config.lineasEjeGraphcantidadLineas.plus(1)) config.lineasEjeGraphColor else Color.Transparent
                } else {
                    Color.Transparent
                }
                HorizontalDivider(thickness = config.lineasEjeGraphSize, color = color)
            }
        }
    }
}