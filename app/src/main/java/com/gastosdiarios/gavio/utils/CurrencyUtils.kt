package com.gastosdiarios.gavio.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formattedCurrency(amount: Double?): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        val formattedAmount = if (amount != null) {
            currencyFormat.format(amount)
        } else {
            currencyFormat.format(0.0)
        }
        val currencySymbol = currencyFormat.currency?.symbol ?: "$"
        return formattedAmount.replace(currencySymbol, "").trim()
    }

    fun convertidorDeTexto(dineroAConvertir: Double): Pair<String, String> {
        val formattedAmount = formattedCurrency(dineroAConvertir)
        val dinero = formattedAmount.substringBeforeLast(",").replace("$", "")

        val separadorDecimal = if (formattedAmount.contains(".")) "." else ","
        val centavos = if (separadorDecimal == ",") {
            formattedAmount.substringAfterLast(".", "").take(2)
        } else {
            formattedAmount.substringAfterLast(",", "").take(2)
        }

        return Pair(dinero, centavos)
    }
}