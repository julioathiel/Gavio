package com.gastosdiarios.gavio.utils

import android.util.Log
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private const val TAG = "dateUtils"
    private const val FORMATO_FECHA_GUION = "yyyy-MM-dd"
    private const val FORMATO_FECHA_BARRA = "dd/MM/yyyy"
    private const val FORMATO_FECHA_PERSONALIZADO = "dd MMM. yyyy"
    fun obtenerFechaActual(): LocalDate {
        //retorna la fecha actual en formato LocalDate 2023-01-01
        return LocalDate.now()
    }
    //retorna el mes actual en formato de texto
    fun currentMonth(): String? {
        // retorena solo esto ene., feb., mar., etc.
        val fechaActual = obtenerFechaActual()
        return fechaActual.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun parsearFechaALocalDate(fechaString: String): LocalDate {
        return LocalDate.parse(fechaString, DateTimeFormatter.ofPattern(FORMATO_FECHA_GUION))
    }

    fun converterFechaABarra(fechaGuion: String): String {
        // fechaGuion sería "2024-04-30"
        val formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_GUION)
        val localDate = LocalDate.parse(fechaGuion, formatter)
        return localDate.format(DateTimeFormatter.ofPattern(FORMATO_FECHA_BARRA))
    }

    fun converterFechaPersonalizada(date: String): String {
        Log.d("fecha actual", "converterFechaPersonalizada = date: $date")
        val partesFecha = date.split("/") // Dividir la fecha en partes: [dia, mes, año]
        if (partesFecha.size == 3) {
            val day = partesFecha[0]
            val month = getNameMes(partesFecha[1].toInt()) // Obtener el nombre del mes
            val year = partesFecha[2]
            return "$day $month. $year" // Formato personalizado: 27 jun. 2024
        } else {
            return "Formato incorrecto"
        }
    }

    fun converterFechaAGuion(date: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern(FORMATO_FECHA_BARRA)
            val localDate = LocalDate.parse(date, formatter)
            localDate.format(DateTimeFormatter.ofPattern(FORMATO_FECHA_GUION))
        } catch (e: DateTimeParseException) {
            Log.e(TAG, "Error al converter la fecha: $date", e)
            ""
        }
    }
    fun converterFechaPersonalizadaPunto(): String {
        val currentDate = obtenerFechaActual()
        val fecha = currentDate.format(DateTimeFormatter.ofPattern(FORMATO_FECHA_PERSONALIZADO))
        return fecha
    }

    fun agregandoUnMes(fechaActual: LocalDate, fechaParseada: LocalDate): String {
        val diff = ChronoUnit.MONTHS.between(fechaParseada, fechaActual).plus(1)
        val mes = fechaParseada.plus(diff, ChronoUnit.MONTHS)
        return mes.toString()
    }

    private fun getNameMes(mes: Int): String {
        return DateFormatSymbols().months[mes - 1].substring(0, 3).lowercase(Locale.ROOT)
            .replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.ROOT)
                else it.toString()
            } // Obtener el nombre del mes en formato corto y capitalizado
    }

    fun obtenerFechaAyer(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val ayer = calendar.time
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatoFecha.format(ayer)
    }
}