package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.CurrencyAmountInputVisualTransformation
import com.gastosdiarios.gavio.data.commons.BotonGastosIngresos
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.CategoriesModel
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    modifier: Modifier,
    showTransaction: Boolean,
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    var cantidadIngresada by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoriesModel?>(null) }
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    if (showTransaction) {
        ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(),
            content = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CommonsSpacer(width = 0.dp, height = 30.dp)
                        TextFieldDinero(cantidadIngresada, modifier, focusRequester) { nuevoValor ->
                            cantidadIngresada = nuevoValor
                        }
                        CommonsSpacer(width = 0.dp, height = 16.dp)

                        val categorySelected: CategoriesModel =
                            menuDesplegable(homeUiState, modifier)
                        selectedCategory = categorySelected
                        CommonsSpacer(width = 0.dp, height = 10.dp)
                        Text(
                            text = stringResource(R.string.descripcion),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        CommonsSpacer(width = 0.dp, height = 10.dp)
                        //Description
                        TextFieldDescription(
                            description = description,
                            modifier = modifier
                        ) { newDescription ->
                            description = newDescription
                        }
                        CommonsSpacer(width = 30.dp, height = 20.dp)

                        BotonGastosIngresos(
                            modifier = modifier.height(51.dp),
                            enabledBotonGastos = homeUiState.enabledButtonGastos,
                            botonActivado = homeUiState.buttonIngresosActivated,
                            onTipoSeleccionado = { tipClass ->
                                if (tipClass == TipoTransaccion.INGRESOS) {
                                    homeViewModel.setIsChecked(TipoTransaccion.INGRESOS)
                                } else {
                                    homeViewModel.setIsChecked(TipoTransaccion.GASTOS)
                                }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            selectedCategory?.let {
                                onClick(
                                    homeUiState,
                                    homeViewModel,
                                    cantidadIngresada,
                                    description,
                                    it,
                                    navController,
                                )
                            }

                            cantidadIngresada = ""
                            description = ""
                            onDismiss()
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                            .align(Alignment.BottomCenter)
                            .height(dimensionResource(id = R.dimen.padding_altura_boton)),
                        enabled = cantidadIngresada.isNotEmpty() && selectedCategory?.name != stringResource(
                            R.string.elige_una_categoria
                        )
                    ) {
                        Text(text = stringResource(R.string.guardar), fontSize = 14.sp)
                    }
                }
            }
        )
    }

    LaunchedEffect(showTransaction) {
        if (showTransaction) {
            focusRequester.requestFocus() // Solicitar el foco en el TextFieldDinero
        }
    }
}

fun onClick(
    homeUiState: HomeUiState,
    homeViewModel: HomeViewModel,
    cantidadIngresada: String,
    description: String,
    selectedCategory: CategoriesModel,
    navController: NavController
) {
   val tipo = homeViewModel.homeUiState.value.tipoTransaccion
   val tipoTransaccion = tipo ?: TipoTransaccion.INGRESOS

    if (homeUiState.fechaElegida == null) {
        homeViewModel.onDialogClose()
        navController.navigateUp()
    } else {
        //mandar tarea
        homeViewModel.cantidadIngresada(cantidadIngresada,tipoTransaccion)
        //creando una transaccion
        homeViewModel.crearTransaction(
            cantidadIngresada,
            selectedCategory.name,
            description,
            selectedCategory.icon,
            tipoTransaccion = tipoTransaccion
        )
        //si la seleccion del usuario es gastos entonces se crea el registro de gastos individuales
        if (TipoTransaccion.GASTOS == homeUiState.tipoTransaccion) {
            //creando categoria individual
            homeViewModel.crearNuevaCategoriaDeGastos(
                selectedCategory.name,
                selectedCategory.icon,
                cantidadIngresada
            )
        }
    }
}

@Composable
fun TextFieldDinero(
    cantidadIngresada: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onTextChanged: (String) -> Unit
) {
    val maxLength = 10
    val textSize by remember { mutableStateOf(50.sp) }

    OutlinedTextField(
        value = cantidadIngresada.take(maxLength),
        onValueChange = {
            if (it.length <= maxLength) {
                onTextChanged(it)
            }
        },
        singleLine = true,
        maxLines = 1,
        modifier = modifier.focusRequester(focusRequester),
        placeholder = {
            Row(modifier, horizontalArrangement = Arrangement.Center) {
                Text(
                    stringResource(R.string._0_00),
                    color = colorResource(id = R.color.grayTres),
                    fontSize = textSize
                )
            }

        },
        visualTransformation = CurrencyAmountInputVisualTransformation(
            fixedCursorAtTheEnd = true
        ),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(20.dp),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = textSize
        )
    )
}

@Composable
fun FormattedCurrencyCash(viewModel: HomeViewModel) {
    val dineroActualState by viewModel.homeUiState.collectAsState()
    val (integerPart, decimalPart) = CurrencyUtils.convertidorDeTexto(
        dineroActualState.dineroActual ?: 0.0
    )
    ObservadorDinero(integerPart, decimalPart)
}