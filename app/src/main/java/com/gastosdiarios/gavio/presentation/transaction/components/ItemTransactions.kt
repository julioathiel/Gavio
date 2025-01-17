package com.gastosdiarios.gavio.presentation.transaction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.EditDeleteAlertDialog
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.data.events_handlers.OnActionsMovimientos
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero
import com.gastosdiarios.gavio.presentation.transaction.TransactionsViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemTransactions(
    item: TransactionModel,
    itemList: List<TransactionModel>,
    viewModel: TransactionsViewModel
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showConfirmationEditar by remember { mutableStateOf(false) }
    var isClicked by remember { mutableStateOf(false) }
    var isLongPressed by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val textColor = if (item.select == true) {
        //si el usuario eligio ingreso, el color de los numeros sera verde
        colorResource(id = R.color.verdeDinero)
    } else MaterialTheme.colorScheme.onSurfaceVariant//sin color

    LaunchedEffect(isClicked) {
        isClicked = false
        isLongPressed = false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                when {
                    isLongPressed -> Color.LightGray // Color de fondo cuando se produce un clic prolongado
                    isClicked -> Color.Gray // Color de fondo cuando se produce un clic normal
                    else -> MaterialTheme.colorScheme.background
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { isExpanded = !isExpanded },
                    onLongPress = {
                        // Manejar el clic prolongado
                        isClicked = true
                        isLongPressed = true
                        showConfirmationDialog = true

                    })
            }
    ) {

        Spacer(modifier = Modifier.padding(start = 16.dp))

        //contenedor de icono
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icono dentro del círculo
            Icon(
                painter = painterResource(id = item.icon.orEmpty().toInt()),
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                tint = MaterialTheme.colorScheme.primary
                )
        }

        Spacer(modifier = Modifier.padding(start = 16.dp))
        //contenedor de titulo y subtitulo
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    // Mostrar "..." si el texto excede una línea
                    overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                )
                if (item.subTitle?.isNotEmpty() == true) {
                    Text(
                        text = item.subTitle,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        // Mostrar "..." si el texto excede una línea
                        overflow = if (isExpanded) TextOverflow.Clip else TextOverflow.Ellipsis,
                    )
                }
            }
            //contenedor de dinero y fecha
            Column(Modifier.padding(end = 8.dp), horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyUtils.formattedCurrency(item.cash?.toDouble()),
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.padding(36.dp))

        if (showConfirmationDialog) {
            EditDeleteAlertDialog(
                onEditClick = {
                    //se vuelve true para mostrar el dialogo
                    showConfirmationEditar = true

                },
                onDeleteClick = {
                    viewModel.onEventHandler(OnActionsMovimientos.DeleteItem(itemList, item))
                },
                onDismiss = {
                    isLongPressed = false
                    showConfirmationDialog = false
                }
            )

        }
        //si presiona editar se abrira otro dialogo para editar
        if (showConfirmationEditar) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = {
                    showConfirmationEditar = false
                    showConfirmationDialog = false
                },
                sheetState = sheetState,
                content = {

                    var cantidadIngresada by remember { mutableStateOf(item.cash.toString()) }
                    var description by remember { mutableStateOf(item.subTitle.toString()) }

                    val spaciness = 16.dp
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.padding(spaciness))

                        TextFieldDinero(cantidadIngresada, Modifier.fillMaxWidth()) { nuevoValor ->
                            cantidadIngresada = nuevoValor
                        }
                        Spacer(modifier = Modifier.padding(spaciness))
                        Text(text = stringResource(id = R.string.descripcion))
                        //Description
                        TextFieldDescription(
                            description = description,
                            modifier = Modifier.fillMaxWidth()
                        ) { newDescription ->
                            description = newDescription
                        }
                        Spacer(modifier = Modifier.size(150.dp))

                        Button(
                            onClick = {
                                // Actualizar dinero y descripción
                                viewModel.onEventHandler(
                                    OnActionsMovimientos.EditItem(
                                        title = item.title.orEmpty(),
                                        nuevoValor = cantidadIngresada,
                                        description = description,
                                        item = item
                                    )
                                )
                                showConfirmationEditar = false
                                cantidadIngresada = ""
                                description = ""
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = cantidadIngresada.isNotEmpty()
                        ) {
                            Text(text = stringResource(id = R.string.guardar))
                        }
                        Spacer(modifier = Modifier.size(24.dp))
                    }
                }
            )
        }
    }
}