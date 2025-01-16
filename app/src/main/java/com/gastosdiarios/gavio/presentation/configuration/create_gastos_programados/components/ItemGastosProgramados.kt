package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsSpacer
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.data.commons.TextFieldDescription
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.enums.Modo
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.categoriaDefault
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.userCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.userCategoriesIngresosList
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosDefaultViewModel
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero
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