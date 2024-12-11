package com.gastosdiarios.gavio.bar_graph_custom

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarGraphConfig<T>(
    var barGraphList: List<T> = emptyList(),
    var dataBarSelectedBarColor: Color = Color.Blue,
    var dataBarUnSelectedBarColor: Color = Color(0xFFF3F3F3),
    var dataBarWidth: Dp = 40.dp,
    var dataBarEspacioEntreBarras: Dp = 8.dp,
    var altura: Dp = 200.dp,
    var etiquetaTextSize: TextUnit = 16.sp,
    var etiquetaTextColor: Color = Color.Red,
    var etiquetaCornerRadius: Float = 30f,
    var roundType: BarType = BarType.CIRCULAR_TYPE,
    var barWidth: Dp = 30.dp,
    var maxGraphValue: Int = 100,
    var minGraphValue: Int = 0,
    var containerTextColor: Color = Color.Green,
    var etiquetaVisible: Boolean = true,
    var etiquetaContainerColor: Color = Color.Gray,
    var etiquetaTranslateY: Float = -5f,
    var etiquetaEjeYVisible: Boolean = true,
    var etiquetaEjeYColor: Color = Color.Black,
    var etiquetaEjeXTextSixe: TextUnit = 14.sp,
    var etiquetaEjeXColor: Color = Color.Black,
    var etiquetaEjeXRotate: Float = 0f,
    var lineaVerticalVisible: Boolean = true,
    var lineaVerticalColor: Color = Color.Black,
    var lineaVerticalWidth: Dp = 1.dp,
    var lineaVerticalHeigth: Dp = 4.dp,
    var viewBarTextSize: TextUnit = 12.sp,
    var viewBarTextColor: Color = Color.Black,
    var viewBarContainerColor: Color = Color.LightGray,
    var viewBarCornerRadius: Float = 60f,
    var viewBarPaddingBottom: Dp = 16.dp,
    var viewBarHeigth: Dp = 80.dp,
    var lineaBaseVisible: Boolean = true,
    var lineaBaseColor: Color = Color.Black,
    var lineasEjeGraphVisible: Boolean = true,
    var lineasEjeGraphSize: Dp = 1.dp,
    var lineasEjeGraphColor: Color = Color(0xFFF3F3F3),
    var lineasEjeGraphcantidadLineas: Int = 5,
    var dataToLabel: (T) -> String = { "" },
    var dataToValue: (T) -> Float = { 0f },
    var dataToMonth: (T) -> String = { "" }
)

fun <T> BarGraphConfig<T>.lineasEjeGraph(
    visible: Boolean,
    size: Dp,
    color: Color,
    cantidadLineas: Int
) = apply {
    this.lineasEjeGraphVisible = visible
    this.lineasEjeGraphSize = size
    this.lineasEjeGraphColor = color
    this.lineasEjeGraphcantidadLineas = cantidadLineas
}

fun <T> BarGraphConfig<T>.etiquetaEjeY(visible: Boolean, color: Color) = apply {
    this.etiquetaEjeYVisible = visible
    this.etiquetaEjeYColor = color
}

fun <T> BarGraphConfig<T>.data(data: List<T>) = apply { this.barGraphList = data }
fun <T> BarGraphConfig<T>.dataBar(
    selectedBarColor: Color,
    unSelectedBarColor: Color,
    width: Dp,
    espacioEntreBarras: Dp
) = apply {
    this.dataBarSelectedBarColor = selectedBarColor
    this.dataBarUnSelectedBarColor = unSelectedBarColor
    this.dataBarWidth = width
    this.dataBarEspacioEntreBarras = espacioEntreBarras
}

fun <T> BarGraphConfig<T>.etiquetaEjeX(textSixe: TextUnit, color: Color, rotate: Float) = apply {
    this.etiquetaEjeXTextSixe = textSixe
    this.etiquetaEjeXColor = color
    this.etiquetaEjeXRotate = rotate
}

fun <T> BarGraphConfig<T>.height(altura: Dp) = apply { this.altura = altura }
fun <T> BarGraphConfig<T>.viewBarText(
    textSize: TextUnit,
    textColor: Color,
    containerColor: Color,
    cornerRadius: Float,
    paddingBottom: Dp,
    heigth: Dp,
) = apply {
    this.viewBarTextSize = textSize
    this.viewBarTextColor = textColor
    this.viewBarContainerColor = containerColor
    this.viewBarCornerRadius = cornerRadius
    this.viewBarPaddingBottom = paddingBottom
    this.viewBarHeigth = heigth
}

fun <T> BarGraphConfig<T>.etiquetaTranslateY(y: Float) = apply { this.etiquetaTranslateY = y }
fun <T> BarGraphConfig<T>.etiqueta(textSize: TextUnit, color: Color) =
    apply {
        this.etiquetaTextSize = textSize
        this.etiquetaTextColor = color
    }

fun <T> BarGraphConfig<T>.etiquetaContainer(color: Color, cornerRadius: Float, visible: Boolean) =
    apply {
        this.etiquetaVisible = visible
        this.etiquetaContainerColor = color
        this.etiquetaCornerRadius = cornerRadius
    }

fun <T> BarGraphConfig<T>.roundType(roundType: BarType) = apply { this.roundType = roundType }
fun <T> BarGraphConfig<T>.lineaBase(visible: Boolean, color: Color) = apply {
    this.lineaBaseVisible = visible
    this.lineaBaseColor = color
}

fun <T> BarGraphConfig<T>.lineaVertical(
    lineaVerticalesVisible: Boolean,
    color: Color,
    witdh: Dp,
    heigth: Dp
) = apply {
    this.lineaVerticalVisible = lineaVerticalesVisible
    this.lineaVerticalColor = color
    this.lineaVerticalWidth = witdh
    this.lineaVerticalHeigth = heigth
}

fun <T> BarGraphConfig<T>.dataToValue(mapper: (T) -> Float) = apply { this.dataToValue = mapper }
fun <T> BarGraphConfig<T>.dataToLabel(mapper: (T) -> String) = apply { this.dataToLabel = mapper }
fun <T> BarGraphConfig<T>.dataToMonth(mapper: (T) -> String) = apply { this.dataToMonth = mapper }

fun <T> BarGraphConfig<T>.build() = this

enum class BarType { CIRCULAR_TYPE, TOP_CURVED }