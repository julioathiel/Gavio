package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

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
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.categoriaDefault
import com.gastosdiarios.gavio.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.userCategoriesGastosList
import com.gastosdiarios.gavio.domain.model.userCategoriesIngresosList
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens.PagerScreenOne
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ContentBottomSheetGastosProgramados
import com.gastosdiarios.gavio.presentation.home.components.TextFieldDinero
import com.gastosdiarios.gavio.presentation.home.components.onClick
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
    val cash = CurrencyUtils.formattedCurrency(item.cash?.toDouble())

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

            Text(
                text = cash,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = "Alarma definida para...",
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            Text(
                text = item.date ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val shouldShowBottomSheet = uiStateDefault.isSelectedEditItem && selectedIndex
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
                    viewModel = viewModel
                )
            }
        )
    }
}

@Composable
fun PagerScreenOneProbando(
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
        val categorySelected = menu(categorySelect)
        selectedCategory = categorySelected
        onSelectedCategory(categorySelected)

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menu(ites: CategoriesModel): CategoriesModel {

    var expanded by remember { mutableStateOf(false) }

    // Recordar la lista de categorías para evitar la recomposición innecesaria
    val categories: List<CategoriesModel> =
        remember(userCategoriesIngresosList, userCategoriesGastosList) {
            categoriaDefault + userCategoriesGastosList + defaultCategoriesGastosList.sortedBy { it.name }
        }
    val selectedItemInitial = categories.find { it.name == ites.name }

    var selectedItem by remember(categories, selectedItemInitial) {
        mutableStateOf(
            selectedItemInitial ?: categories.first ()
        )
    }

    var colorIcon = if (selectedItem == categories.first()) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        Alignment.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedItem.name,
                onValueChange = {},
                leadingIcon = {
                    Image(
                        painter = painterResource(id = selectedItem.icon),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(colorIcon)
                    )
                },
                readOnly = true,
                trailingIcon = {
                    IconButton(
                        onClick = { expanded != expanded },
                        modifier = Modifier.semantics {
                            contentDescription = if (expanded) "cerrar menu" else "abrir menu"
                        }
                    ) {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "Trailing icon for exposed dropdown menu",
                            Modifier.rotate(if (expanded) 180f else 0f)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray

                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { itemCategory ->

                    colorIcon = if (itemCategory == categories.first()) Color.Transparent
                    else MaterialTheme.colorScheme.onSurfaceVariant

                    DropdownMenuItem(
                        text = { Text(text = itemCategory.name) },
                        onClick = {
                            selectedItem = itemCategory
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = itemCategory.icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = colorIcon
                            )

                        }
                    )
                }
            }
        }
    }

    return selectedItem
}
