package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosDefaultViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ItemGastosProgramados(
    item: GastosProgramadosModel,
    selectedIndex: Boolean,
    onClick: () -> Unit,
    onLongItemClick: () -> Unit,
    viewModel: CreateGastosDefaultViewModel
) {
    val uiStateDefault by viewModel.uiStateDefault.collectAsState()
    val cash = CurrencyUtils.formattedCurrency(item.cash?.toDouble() ?: 0.0)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldShowBottomSheet = uiStateDefault.isSelectedEditItem && selectedIndex

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongItemClick() }
            ),
        colors = CardDefaults.cardColors(
            containerColor =
            if (selectedIndex)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainer
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = item.date ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = cash,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            Row(modifier = Modifier.fillMaxWidth()) {

                ProfileIcon(
                    drawableResource = item.icon?.toInt() ?: R.drawable.ic_info,
                    description = item.title ?: "",
                    sizeBox = 40,
                    colorCircle = Color.Transparent,
                    colorIcon = MaterialTheme.colorScheme.primary
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.title ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = item.subTitle ?: "",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

        }
    }

    if (shouldShowBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.isEditItemFalse()
                viewModel.isActivatedFalse()
            },
            sheetState = sheetState,
            content = {
                ContentBottomSheetGastosProgramados(
                    item = item,
                    onDismiss = { viewModel.onDismissSet(false) },
                    categoryTypes = CategoryTypeEnum.GASTOS,
                    viewModel = viewModel,
                    modo = Modo.EDITAR
                )
            }
        )
    }
}