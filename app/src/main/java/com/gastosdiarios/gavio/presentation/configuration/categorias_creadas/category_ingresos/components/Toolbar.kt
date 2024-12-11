package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.CategoryIngresosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarCategories(viewModel: CategoryIngresosViewModel, onClickAction: () -> Unit) {
    val activation = viewModel.isActivated.value
    var isDeleteAll by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.categorias_nuevas)) },
        actions = {
            if (activation) {
                IconButton(onClick = { isDeleteAll = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_option),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = isDeleteAll,
                    onDismissRequest = { isDeleteAll= !isDeleteAll }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.eliminar_todo)) },
                        onClick = { onClickAction() }
                    )

                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
    )
}