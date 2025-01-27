package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosProgramadosViewModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens.listHorizontalPagerScreens
import com.gastosdiarios.gavio.utils.DateUtils
import kotlinx.coroutines.launch

@Composable
fun ContentBottomSheetGastosProgramados(
    item: GastosProgramadosModel,
    onDismiss: () -> Unit,
    categoryTypes: TipoTransaccion,
    viewModel: CreateGastosProgramadosViewModel,
    modo: Modo,
) {
    var enabledButton by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var selectedCategory by remember { mutableStateOf<CategoriesModel?>(null) }
    var selectedDate by remember { mutableStateOf(item.date ?: "") }
    var dineroProgramado by remember { mutableStateOf(item.cash ?: "") }
    var subTitleProgramado by remember { mutableStateOf(item.subTitle ?: "") }

    val list = listHorizontalPagerScreens(
        item = item,
        onDineroProgramado = { dineroProgramado = it },
        onSubTitleProgramado = { subTitleProgramado = it },
        onSelectedCategory = { selectedCategory = it },
        onEnabledButtonChanged = { enabledButton = it },
        selectedDate = { selectedDate = DateUtils.formatSelectedDate(it) },
        focusRequester = focusRequester
    )


    val state = rememberPagerState(initialPage = 0, pageCount = { list.size })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = state,
        modifier = Modifier
    ) { page ->
        Box(
            Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        ) {
            Column(
                Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                list[state.currentPage].content(PaddingValues())
            }

            when (page) {
                0 -> {
                    Button(
                        onClick = {
                            scope.launch {
                                val nextPage = (state.currentPage + 1) % state.pageCount
                                state.scrollToPage(nextPage)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .height(dimensionResource(id = R.dimen.padding_altura_boton))
                            .fillMaxWidth(),
                        enabled = enabledButton
                    ) {
                        Text("Siguiente")
                    }
                    LaunchedEffect(focusRequester) {
                        focusRequester.requestFocus()
                    }
                }

                1 -> {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(dimensionResource(id = R.dimen.padding_medium)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val nextPage = (state.currentPage - 1) % state.pageCount
                                    state.scrollToPage(nextPage)
                                }
                            },
                            modifier = Modifier.height(dimensionResource(id = R.dimen.padding_altura_boton)),
                        ) {
                            Text("Atras")
                        }

                        val newValor = item.copy(
                            title = selectedCategory?.name,
                            subTitle = subTitleProgramado,
                            cash = dineroProgramado,
                            date = selectedDate,
                            icon = selectedCategory?.icon.toString(),
                            categoryType = categoryTypes
                        )
                        Log.d("TAGG", "ContentBottomSheetGastosProgramados: $newValor")
                        RealizarAccion(modo, viewModel, newValor, selectedDate, onDismiss)
                    }
                }
            }
        }
    }
}

@Composable
fun RealizarAccion(
    modo: Modo,
    viewModel: CreateGastosProgramadosViewModel,
    item: GastosProgramadosModel,
    selectedDate: String,
    onDismiss: () -> Unit
) {
    when (modo) {
        Modo.AGREGAR -> {
            Log.d("TAGG", "RealizarAccion: $modo")
            Button(
                modifier = Modifier.height(dimensionResource(id = R.dimen.padding_altura_boton)),
                onClick = {
                    viewModel.create(item)
                    onDismiss()
                },
                enabled = selectedDate.isNotEmpty()
            ) {
                Text("Guardar")
            }
        }

        Modo.EDITAR -> {
            Log.d("TAGG", "RealizarAccion: $modo")
            Button(
                modifier = Modifier.height(dimensionResource(id = R.dimen.padding_altura_boton)),
                onClick = {
                    viewModel.update(item)
                    onDismiss()
                },
                enabled = selectedDate.isNotEmpty()
            ) {
                Text("Guardar")
            }
        }
    }
}