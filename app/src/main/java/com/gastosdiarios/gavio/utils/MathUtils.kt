package com.gastosdiarios.gavio.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object MathUtils {
    fun getLimitePorDia(currentMoney: Double, diasRestantes: Int): Double {
        return if (currentMoney !=0.0 && diasRestantes != 0) {
            currentMoney / diasRestantes.toDouble()
        } else {
            0.0
        }
    }

    fun sumarBigDecimal(nuevoValor: Double, valorExistente: Double): Double {
        return BigDecimal(valorExistente + nuevoValor).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun restarBigDecimal(alto: Double, menos: Double): Double {
        return BigDecimal(alto - menos).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun minus(nuevoValor: String, valorExistente: String): Double {
        return nuevoValor.toDouble().minus(valorExistente.toDouble())
    }

    fun bigDecimalToDouble(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun calcularProgresoRelativo(totalIngresos: Double?, totalGastos: Double?): Float {
        val ingresosTotales = totalIngresos ?: 0.0
        val gastosTotales = totalGastos ?: 0.0
        if (ingresosTotales > 0) {
            return (gastosTotales / ingresosTotales).toFloat()
        }
        return 0.0f
    }

    fun formattedPorcentaje(progresoRelativo: Float): String {
        return String.format(Locale.US, "%.0f", progresoRelativo * 100)
    }
}