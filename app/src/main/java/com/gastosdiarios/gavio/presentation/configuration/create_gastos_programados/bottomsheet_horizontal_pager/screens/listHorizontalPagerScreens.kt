package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import com.gastosdiarios.gavio.data.domain.model.CategoriesModel
import com.gastosdiarios.gavio.data.domain.model.Time
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.data_class.ListHorizontalPagerScreen

@Composable
fun listHorizontalPagerScreens(
    item: GastosProgramadosModel,
    onDineroProgramado: (String) -> Unit,
    onSubTitleProgramado: (String) -> Unit,
    onSelectedCategory: (CategoriesModel) -> Unit,
    onEnabledButtonChanged: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    selectedDate: (Long?) -> Unit,
    time: (MutableState<Time>) -> Unit
): List<ListHorizontalPagerScreen> {
    return listOf(
        ListHorizontalPagerScreen(content = {
                PagerScreenOne(
                    item = item,
                    onDineroProgramado,
                    onSubTitleProgramado,
                    onSelectedCategory,
                    onEnabledButtonChanged,
                    focusRequester = focusRequester
                )
        }),
        ListHorizontalPagerScreen(content = {
            PagersScreenTwo(item = item, selectedDate = selectedDate)
        }),
        ListHorizontalPagerScreen(content = {
            PagerScreenThree(item = item, time = time)
        })
    )
}


