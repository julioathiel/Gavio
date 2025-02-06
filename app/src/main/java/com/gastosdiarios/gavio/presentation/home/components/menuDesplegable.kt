package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.data.ui_state.HomeUiState
import com.gastosdiarios.gavio.data.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.data.domain.model.CategoriesModel
import com.gastosdiarios.gavio.data.domain.model.categoriaDefault
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesGastosList
import com.gastosdiarios.gavio.data.domain.model.defaultCategoriesIngresosList
import com.gastosdiarios.gavio.data.domain.model.userCategoriesGastosList
import com.gastosdiarios.gavio.data.domain.model.userCategoriesIngresosList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun menuDesplegable(homeUiState: HomeUiState, modifier: Modifier): CategoriesModel {

    var expanded by remember { mutableStateOf(false) }

    // Recordar la lista de categorías para evitar la recomposición innecesaria
    val categories: List<CategoriesModel> = remember(
        homeUiState.tipoTransaccion,
        userCategoriesIngresosList,
        userCategoriesGastosList
    ) {
        if (homeUiState.tipoTransaccion == TipoTransaccion.INGRESOS) {
            categoriaDefault +
                    userCategoriesIngresosList +
                    defaultCategoriesIngresosList
                        .sortedBy { it.name }
        } else {
            categoriaDefault +
                    userCategoriesGastosList +
                    defaultCategoriesGastosList
                        .sortedBy { it.name }
        }
    }


    var selectedItem by remember(categories) { mutableStateOf(categories.first()) }

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
            modifier = modifier
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