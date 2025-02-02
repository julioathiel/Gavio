package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListItem(
    modifier: Modifier,
    viewModel: HomeViewModel,
    item: GastosProgramadosModel,
    onPagarItem: () -> Unit,
    onRemoveItem: () -> Unit
) {

    val isSelected by remember { mutableStateOf(false) }
    val cash = CurrencyUtils.formattedCurrency(item.cash?.toDouble())
    var showBottomSheet by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        Card(
            modifier = modifier.semantics { selected = isSelected }
        ) {

            Column(
                modifier = modifier
                    .wrapContentSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {

                Row(
                    modifier = modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cash,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))

                    )
                    //borrando item
                    IconButton(
                        onClick = { onRemoveItem() },
                        modifier = Modifier.clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "cerrar item",
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(vertical = 10.dp))

                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = item.title.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                        )
                        if (item.subTitle?.isNotEmpty() == true) {
                            Text(
                                text = item.subTitle.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

            }

        }
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small_4dp)))
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            ProfileIcon(
                drawableResource = item.icon.orEmpty().toInt(),
                description = item.title ?: "",
                sizeBox = 50,
                boxRounded = 10,
                colorBackground = MaterialTheme.colorScheme.surfaceContainer,
                colorIcon = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { showBottomSheet = true }) {
                Text(text = stringResource(R.string.editar))
            }
            Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)))
            Button(
                onClick = { onPagarItem() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = stringResource(R.string.pagar))
            }
        }
    }

    if (showBottomSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            content = {
                var cantidadIngresada by remember { mutableStateOf(item.cash.toString()) }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                ) {
                    Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)))

                    TextFieldDinero(
                        cantidadIngresada, Modifier.fillMaxWidth(),
                        focusRequester = focusRequester
                    ) { nuevoValor ->
                        cantidadIngresada = nuevoValor
                    }
                    Spacer(modifier = Modifier.padding(50.dp))

                    Button(
                        onClick = {
                            viewModel.pagarItem(item.copy(cash = cantidadIngresada))
                            showBottomSheet = false

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = cantidadIngresada.isNotEmpty()
                    ) {
                        Text(text = "Pagar")
                    }
                    Spacer(modifier = Modifier.size(24.dp))
                }
            }
        )

    }
}