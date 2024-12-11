package com.gastosdiarios.gavio.data

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.lang.Integer.max
import java.text.DecimalFormat

class CurrencyAmountInputVisualTransformation(
    private val fixedCursorAtTheEnd: Boolean = true,
    private val numberOfDecimals: Int = 2
) : VisualTransformation {

    private val symbols = DecimalFormat().decimalFormatSymbols

    override fun filter(text: AnnotatedString): TransformedText {
        val inputText = text.text //el texto que ingresa el usuario
        val intPart: String
        var fractionPart = ""

       val separadorConComa = symbols.groupingSeparator//separador con comas
        val decimalSeparator = symbols.decimalSeparator //separador con punto
        val zero = symbols.zeroDigit //agregador de 00



        if (inputText.contains(decimalSeparator)) {//verificando si contiene separador decimal osea ( . )
            val parts = inputText.split(decimalSeparator)//divide el unputext en dos partes
            intPart = parts[0].reversed()//se invierte desde la posicion 0 para que queden en elorden correcto
                .chunked(3)//se toman grupos de 3
                .joinToString(decimalSeparator.toString())//se le agrega el separador de miles osea la ( , )
                .reversed() //se invierte para quedar en el orden correcto
            fractionPart = parts[1].take(numberOfDecimals)
        } else {
            intPart = inputText.reversed()
                .chunked(3)//se toman grupos de 3
                .joinToString(decimalSeparator.toString())
                .reversed()
        }

        val formattedNumber = "$intPart${if (inputText.contains(decimalSeparator)) separadorConComa else ""}$fractionPart"

        // Crea un nuevo AnnotatedString con el número formateado
        val newText = AnnotatedString(
            text = formattedNumber,
            spanStyles = text.spanStyles,
            paragraphStyles = text.paragraphStyles
        )
        // Crea el mapeo de desplazamiento según las reglas de formato de moneda
        val offsetMapping = if (fixedCursorAtTheEnd) {
            // Mantiene el cursor al final del texto formateado
            FixedCursorOffsetMapping(
                contentLength = inputText.length,
                formattedContentLength = formattedNumber.length
            )
        } else {
            // Permite que el cursor se mueva según corresponda
            MovableCursorOffsetMapping(
                unmaskedText = text.toString(),
                maskedText = newText.toString(),
                decimalDigits = numberOfDecimals
            )
        }
        // Devuelve el texto transformado y el mapeo de desplazamiento
        return TransformedText(newText, offsetMapping)
    }

    // Clase interna para el mapeo de desplazamiento con cursor fijo al final
    private class FixedCursorOffsetMapping(
        private val contentLength: Int,
        private val formattedContentLength: Int,
    ) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = formattedContentLength
        override fun transformedToOriginal(offset: Int): Int = contentLength
    }

    // Clase interna para el mapeo de desplazamiento con cursor móvil
    private class MovableCursorOffsetMapping(
        private val unmaskedText: String,
        private val maskedText: String,
        private val decimalDigits: Int
    ) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int =
            when {
                unmaskedText.length <= decimalDigits -> {
                    maskedText.length - (unmaskedText.length - offset)
                }
                else -> {
                    offset + offsetMaskCount(offset, maskedText)
                }
            }

        override fun transformedToOriginal(offset: Int): Int =
            when {
                unmaskedText.length <= decimalDigits -> {
                    max(unmaskedText.length - (maskedText.length - offset), 0)
                }
                else -> {
                    offset - maskedText.take(offset).count { !it.isDigit() }
                }
            }

        private fun offsetMaskCount(offset: Int, maskedText: String): Int {
            var maskOffsetCount = 0
            var dataCount = 0
            for (maskChar in maskedText) {
                if (!maskChar.isDigit()) {
                    maskOffsetCount++
                } else if (++dataCount > offset) {
                    break
                }
            }
            return maskOffsetCount
        }
    }
}