package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components


import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.domain.enums.Modo
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.Alarm
import com.gastosdiarios.gavio.data.domain.model.CategoriesModel
import com.gastosdiarios.gavio.data.domain.model.Time
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosProgramadosViewModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens.listHorizontalPagerScreens
import com.gastosdiarios.gavio.utils.DateUtils
import com.gastosdiarios.gavio.utils.setUpAlarm
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

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
    var timeInMillis by remember {
        mutableStateOf(
            Time(
                hour = item.hour ?: 0,
                minute = item.minute ?: 0
            )
        )
    }
    var dineroProgramado by remember { mutableStateOf(item.cash ?: "") }
    var subTitleProgramado by remember { mutableStateOf(item.subTitle ?: "") }


    val list = listHorizontalPagerScreens(
        item = item,
        onDineroProgramado = { dineroProgramado = it },
        onSubTitleProgramado = { subTitleProgramado = it },
        onSelectedCategory = { selectedCategory = it },
        onEnabledButtonChanged = { enabledButton = it },
        selectedDate = { selectedDate = DateUtils.formatSelectedDate(it) },
        focusRequester = focusRequester,
        time = { timeInMillis = Time(it.value.hour, it.value.minute) }
    )


    val state = rememberPagerState(initialPage = 0, pageCount = { list.size })
    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = state,
        modifier = Modifier,
        pageContent = { page ->
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


                            Button(
                                onClick = {
                                    scope.launch {
                                        val nextPage = (state.currentPage + 1) % state.pageCount
                                        state.scrollToPage(nextPage)
                                    }
                                },
                                modifier = Modifier
                                    .height(dimensionResource(id = R.dimen.padding_altura_boton))
                                    .fillMaxWidth(),
                                enabled = enabledButton
                            ) {
                                Text("Siguiente")
                            }
                        }
                    }

                    2 -> {
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
                                select = false,
                                date = selectedDate,
                                icon = selectedCategory?.icon,
                                categoryType = categoryTypes,
                                hour = timeInMillis.hour,
                                minute = timeInMillis.minute
                            )

                            RealizarAccion(
                                modo, viewModel, newValor, selectedDate,
                                timeInMillis, onDismiss
                            )
                        }
                    }

                }
            }
        }
    )
}

@Composable
fun RealizarAccion(
    modo: Modo,
    viewModel: CreateGastosProgramadosViewModel,
    item: GastosProgramadosModel,
    selectedDate: String,
    timeInMillis: Time,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    when (modo) {
        Modo.AGREGAR -> {

            Button(
                modifier = Modifier.height(dimensionResource(id = R.dimen.padding_altura_boton)),
                onClick = {
                    viewModel.create(item)
                    val time = DateUtils.convertDateAndTimeToMillis(
                        selectedDate,
                        item.hour ?: timeInMillis.hour,
                        item.minute ?: timeInMillis.minute
                    )

                    val alarm = Alarm(
                        id = time,
                        icon = item.icon.toString().toIntOrNull() ?: 0,
                        title = item.title ?: "",
                        message = item.subTitle ?: "",
                        timeInMillis = time,
                        gastosProgramadosId = item.uid ?: ""
                    )
                    setUpAlarm(context, alarm)
                    onDismiss()
                },
                enabled = timeInMillis.hour != 0 || timeInMillis.minute != 0
            ) {
                Text("Guardar")
            }
        }

        Modo.EDITAR -> {
            Button(
                modifier = Modifier.height(dimensionResource(id = R.dimen.padding_altura_boton)),
                onClick = {
                    viewModel.update(item)
                    val time: Long = DateUtils.convertDateAndTimeToMillis(
                        selectedDate,
                        item.hour ?: 0,
                        item.minute ?: 0
                    )

                    val alarm = Alarm(
                        id = time,
                        icon = item.icon ?: 0,
                        title = item.title ?: "",
                        message = item.subTitle ?: "",
                        timeInMillis = time,
                        gastosProgramadosId = item.uid ?: "",
                        cashGastosprogramadosId = item.cash ?: ""
                    )
                    setUpAlarm(context, alarm)
                    onDismiss()
                },
                  enabled = timeInMillis.hour != 0 || timeInMillis.minute != 0
            ) {
                Text("Guardar")
            }
        }
    }
}