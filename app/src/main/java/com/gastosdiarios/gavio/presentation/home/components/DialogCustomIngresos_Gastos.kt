package com.gastosdiarios.gavio.presentation.home.components

import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.CurrencyAmountInputVisualTransformation
import com.gastosdiarios.gavio.data.commons.BotonGastosIngresos
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    modifier: Modifier,
    showDialogTransaction: Boolean,
    onDismiss: () -> Unit,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    var cantidadIngresada by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory: CategoriesModel

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    if (showDialogTransaction) {
        ModalBottomSheet(onDismissRequest = { onDismiss() }, sheetState = sheetState,
            content = {
                Column(
                    modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonsSpacer(width = 0.dp, height = 16.dp)
                    TextFieldDinero(cantidadIngresada, modifier) { nuevoValor ->
                        cantidadIngresada = nuevoValor
                    }
                    CommonsSpacer(width = 0.dp, height = 16.dp)
                    //DropDown para seleccionar la categoria
                    selectedCategory = menuDesplegable(homeUiState, modifier)
                    CommonsSpacer(width = 0.dp, height = 30.dp)
                    Text(
                        text = stringResource(R.string.descripcion),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    //Description
                    TextFieldDescription(
                        description = description,
                        modifier = modifier
                    ) { newDescription ->
                        description = newDescription
                    }
                    CommonsSpacer(width = 30.dp, height = 30.dp)

                    BotonGastosIngresos(
                        modifier = modifier.height(51.dp),
                        enabledBotonGastos = homeUiState.enabledButtonGastos,
                        botonActivado = homeUiState.buttonIngresosActivated,
                        onTipoSeleccionado = { tipClass ->
                            if (tipClass == CategoryTypeEnum.INGRESOS) {
                                homeViewModel.setIsChecked(true)
                            } else {
                                homeViewModel.setIsChecked(false)
                            }
                        }
                    )
                    CommonsSpacer(width = 0.dp, height = 100.dp)
                    Button(
                        onClick = {
                            onClick(
                                homeUiState,
                                homeViewModel,
                                cantidadIngresada,
                                description,
                                selectedCategory,
                                navController
                            )
                            cantidadIngresada = ""
                            description = ""
                            onDismiss()
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    //showBottomSheet = false
                                    onDismiss()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp),
                        enabled = cantidadIngresada.isNotEmpty() && selectedCategory.name != stringResource(
                            R.string.elige_una_categoria
                        )
                    ) {
                        Text(text = stringResource(R.string.guardar), fontSize = 14.sp)
                    }
                    CommonsSpacer(width = 16.dp, height = 16.dp)
                }
            }
        )
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
    if (homeUiState.fechaElegidaBarra == null) {
        homeViewModel.onDialogClose()
        navController.navigateUp()
    } else {
        //mandar tarea
        homeViewModel.cantidadIngresada(
            cantidadIngresada,
            homeUiState.isChecked
        )
        //creando una transaccion
        homeViewModel.crearTransaction(
            cantidadIngresada,
            selectedCategory.name,
            description,
            selectedCategory.icon,
            homeUiState.isChecked
        )
        //si la seleccion del usuario es gastos entonces se crea el registro de gastos individuales
        if (!homeUiState.isChecked) {
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
    onTextChanged: (String) -> Unit
) {
    val maxLength = 10
    var textSize by remember { mutableStateOf(60.sp) }
    var textFieldWidth by remember { mutableFloatStateOf(0f) }

    OutlinedTextField(
        value = cantidadIngresada.take(maxLength),
        onValueChange = {
            if (it.length <= maxLength) {
                onTextChanged(it)
            }
        },
        singleLine = true,
        maxLines = 1,
        modifier = modifier,
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
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
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