package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.menuDesplegableGastosProgramados
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero

@Composable
fun PagerScreenOne(
    item: GastosProgramadosModel,
    onDineroProgramado: (String) -> Unit,
    onSubTitleProgramado: (String) -> Unit,
    onSelectedCategory: (CategoriesModel) -> Unit,
    onEnabledButtonChanged: (Boolean) -> Unit,
    focusRequester: FocusRequester
) {
    var cantidadIngresada by remember { mutableStateOf(item.cash ?: "") }
    var subTitle by remember { mutableStateOf(item.subTitle ?: "") }
    var selectedCategory by remember { mutableStateOf<CategoriesModel?>(null) }
    val categorySelect = CategoryGastos(name = item.title ?: "", icon = item.icon?.toInt() ?: 0)

    Column(
        Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        CommonsSpacer(width = 0.dp, height = 30.dp)

        TextFieldDinero(cantidadIngresada, Modifier.fillMaxWidth(), focusRequester) { nuevoValor ->
            cantidadIngresada = nuevoValor
            onDineroProgramado(cantidadIngresada)
        }
        CommonsSpacer(width = 0.dp, height = 30.dp)
        //DropDown para seleccionar la categoria
        Text(
            text = stringResource(R.string.selecciona_un_icono),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        val categorySelected = menuDesplegableGastosProgramados(categorySelect)
        selectedCategory = categorySelected
        onSelectedCategory(selectedCategory!!)

        CommonsSpacer(width = 0.dp, height = 30.dp)
        Text(
            text = stringResource(R.string.descripcion),
            modifier = Modifier.align(Alignment.Start)
        )
        //Description
        TextFieldDescription(description = subTitle, modifier = Modifier.fillMaxWidth()) {
            subTitle = it
            onSubTitleProgramado(subTitle)
        }
    }
    val enabledButton =
        cantidadIngresada.isNotEmpty() && selectedCategory?.name != stringResource(R.string.elige_una_categoria)

    LaunchedEffect(enabledButton) {
        onEnabledButtonChanged(enabledButton)
    }
}

