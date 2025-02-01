package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.CategoryViewModel

@Composable
fun ToolbarCategoriasNuevas(
    uiStateDefault: CategoryDefaultModel,
    viewModel: CategoryViewModel,
    onClickAction: () -> Unit,
    onBack: () -> Unit
) {
    viewModel.isActivatedTrue()
    var showDropdown by remember { mutableStateOf(false) }

    TopAppBarOnBack(
        title = stringResource(R.string.categorias_nuevas),
        containerColor = MaterialTheme.colorScheme.surface,
        actions = {
            if (uiStateDefault.isActivated) {
                IconButton(onClick = { showDropdown = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu),
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = !showDropdown }) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.eliminar_todo)) },
                        onClick = {
                            onClickAction()
                            showDropdown = false
                        }
                    )
                }
            }
        },
        onBack = { onBack() }
    )
}