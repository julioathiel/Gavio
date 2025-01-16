package com.gastosdiarios.gavio.utils

import java.text.DateFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.TimeUnit

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

    fun toLocalDate(date: String): LocalDate {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun converterFechaPersonalizada(date: String): String {
        val partesFecha = date.split("-") // Dividir la fecha en partes: [dia, mes, año]
        if (partesFecha.size == 3) {
            val day = partesFecha[2]
            val month = getNameMes(partesFecha[1].toInt()) // Obtener el nombre del mes
            val year = partesFecha[0]
            return "$day $month. $year" // Formato personalizado: 27 jun. 2024
        } else {
            return "Formato incorrecto"
        }
    }

    fun agregandoUnMes(fechaActual: LocalDate, fechaParseada: LocalDate): LocalDate {
        val diff = ChronoUnit.MONTHS.between(fechaParseada, fechaActual).plus(1)
        return fechaParseada.plus(diff, ChronoUnit.MONTHS)
    }

    private fun getNameMes(mes: Int): String {
        return DateFormatSymbols().months[mes - 1].substring(0, 3).lowercase(Locale.ROOT)
            .replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.ROOT)
                else it.toString()
            }
    }

    fun obtenerFechaAyer(): LocalDate {
        return obtenerFechaActual().minusDays(1)
    }

    fun isDateSelectableRestrictMinMax(utcTimeMillis: Long, maxDates: Int): Boolean {
       val now = obtenerFechaActual()
        val diasMaximos = TimeUnit.DAYS.toMillis(maxDates.toLong())
        val minDate = now.atTime(0, 0, 0, 0)
            .toEpochSecond(ZoneOffset.UTC) * 1000
        val maxDate = minDate + diasMaximos
        return utcTimeMillis in minDate..maxDate
    }

    fun isDateSelectableRestrictMin(utcTimeMillis: Long): Boolean {
        val now:LocalDate = obtenerFechaActual()
        val minDate:Long = now.atTime(0, 0, 0, 0).toEpochSecond(ZoneOffset.UTC) * 1000
        return utcTimeMillis >= minDate
    }

    fun formatSelectedDateGuion(it: Long?): String {
        return it?.let {
            val zonaHoraria = ZoneId.systemDefault()
            val localDate = Instant.ofEpochMilli(it).atZone(zonaHoraria)
            val day = localDate.dayOfMonth.plus(1).toString().padStart(2, '0')
            val month = localDate.monthValue.toString().padStart(2, '0')
            "${localDate.year}-$month-${day}"
        } ?: ""
    }


}