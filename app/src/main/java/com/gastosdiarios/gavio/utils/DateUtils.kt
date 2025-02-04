package com.gastosdiarios.gavio.utils

import android.icu.util.Calendar
import android.util.Log
import androidx.core.graphics.set
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val TAG = "DateUtils"

    fun obtenerFechaActual(): LocalDate {
        //retorna la fecha actual en formato LocalDate 2023-01-01
        return LocalDate.now()
    }

    fun currentMonth(): String? {
        //retorna el mes actual en formato de texto
        // ene., feb., mar., etc.
        val fechaActual = obtenerFechaActual()
        return fechaActual.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    fun converterFechaPersonalizada(date: String): String {
        val partesFecha = date.split("-") // Dividir la fecha en partes: [dia, mes, año]
        if (partesFecha.size == 3) {
            val day = partesFecha[2]
            val month = getNameMes(partesFecha[1].toInt()) // Obtener el nombre del mes
            val year = partesFecha[0]
            return "$day $month.$year" // Formato personalizado: 27 jun.2024
        } else {
            return "Formato incorrecto"
        }
    }

    fun agregandoUnMes(fechaActual: LocalDate, fechaParseada: LocalDate): LocalDate {
        val diff = ChronoUnit.MONTHS.between(fechaParseada, fechaActual).plus(1)
        val mes = fechaParseada.plus(diff, ChronoUnit.MONTHS)
        return mes
    }

    private fun getNameMes(mes: Int): String {
        return DateFormatSymbols().months[mes - 1].substring(0, 3).lowercase(Locale.ROOT)
            .replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.ROOT)
                else it.toString()
            } // Obtener el nombre del mes en formato corto y capitalizado
    }

    fun isDateSelectableRestrictMin(utcTimeMillis: Long): Boolean {
        val now = obtenerFechaActual()
        val minDate = now.atTime(0, 0, 0, 0).toEpochSecond(ZoneOffset.UTC) * 1000
        return utcTimeMillis >= minDate
    }

    fun isDateSelectableRestrictMinMax(utcTimeMillis: Long, maxDates: Int): Boolean {
        val now = obtenerFechaActual()
        val diasMaximos = TimeUnit.DAYS.toMillis(maxDates.toLong())
        val minDate = now.atTime(0, 0, 0, 0).toEpochSecond(ZoneOffset.UTC) * 1000
        val maxDate = minDate + diasMaximos
        return utcTimeMillis in minDate..maxDate
    }

    fun formatSelectedDate(selectedDateMillis: Long?): String {
        return selectedDateMillis?.let {
            val zonaHoraria = ZoneId.systemDefault()
            val localDate = Instant.ofEpochMilli(it).atZone(zonaHoraria).toLocalDate()
            val day = localDate.dayOfMonth.plus(1).toString().padStart(2, '0')
            val month = localDate.monthValue.toString().padStart(2, '0')
            "${localDate.year}-$month-${day}"
        } ?: ""
    }

    fun toLocalDate(fecha: String): LocalDate {
        return LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    //convierte a un valor numerico
    fun convertDateAndTimeToMillis(date: String, hour: Int, minute: Int): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(date) ?: return 0
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}