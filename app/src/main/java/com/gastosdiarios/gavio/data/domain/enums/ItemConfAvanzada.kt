package com.gastosdiarios.gavio.data.domain.enums

import androidx.annotation.DrawableRes
import com.gastosdiarios.gavio.R

enum class ItemConfAvanzada(
    @get:DrawableRes
    val title: Int,
    val description: Int,
) {
    SEGURIDAD(R.string.seguridad_title, R.string.seguridad_description)
}