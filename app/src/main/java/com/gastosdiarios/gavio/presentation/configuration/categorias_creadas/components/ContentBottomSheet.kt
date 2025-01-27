package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.CategoryViewModel

@Composable
fun ContentBottomSheet(
    onDismiss: () -> Unit,
    categoryTypes: TipoTransaccion,
    uiStateDefault: CategoryDefaultModel,
    viewModel: CategoryViewModel,
) {
    val enabledButton =
        uiStateDefault.titleBottomSheet.isNotEmpty() && uiStateDefault.selectedCategory != null

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
    ) {
        Text(
            text = stringResource(R.string.selecciona_un_icono),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider()

        CategoryListGastos(iconSelected = uiStateDefault.selectedCategory) { icon ->
            viewModel.selectedIcon(icon)
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(R.string.nuevo_titulo),
            style = MaterialTheme.typography.labelMedium
        )
        OutlinedTextField(
            value = uiStateDefault.titleBottomSheet,
            onValueChange = { viewModel.actualizarTitulo(it) },
            modifier = Modifier
                .fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        )
        Spacer(modifier = Modifier.padding(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(10.dp))
        Button(
            onClick = {
                when(categoryTypes){
                    TipoTransaccion.INGRESOS ->{
                        if (uiStateDefault.isSelectedEditItem) {
                            viewModel.actualizandoItem(
                                UserCreateCategoryModel(
                                    uid = uiStateDefault.uid,
                                    categoryName = uiStateDefault.titleBottomSheet,
                                    categoryIcon = uiStateDefault.selectedCategory!!.icon.toString(),
                                    categoryType = TipoTransaccion.INGRESOS
                                )
                            )
                        } else {
                            //para crear un item de ingresos nuevo
                            viewModel.createNewCategory(
                                UserCreateCategoryModel(
                                    categoryName = uiStateDefault.titleBottomSheet,
                                    categoryIcon = uiStateDefault.selectedCategory!!.icon.toString(),
                                    categoryType = TipoTransaccion.INGRESOS
                                )
                            )
                        }
                    }
                    TipoTransaccion.GASTOS ->{
                        if (uiStateDefault.isSelectedEditItem) {
                            viewModel.actualizandoItem(
                                UserCreateCategoryModel(
                                    uid = uiStateDefault.uid,
                                    categoryName = uiStateDefault.titleBottomSheet,
                                    categoryIcon = uiStateDefault.selectedCategory!!.icon.toString(),
                                    categoryType = TipoTransaccion.GASTOS
                                )
                            )
                        } else {
                            //para crear un item de gastos nuevo
                            viewModel.createNewCategory(
                                UserCreateCategoryModel(
                                    categoryName = uiStateDefault.titleBottomSheet,
                                    categoryIcon = uiStateDefault.selectedCategory!!.icon.toString(),
                                    categoryType = TipoTransaccion.GASTOS
                                )
                            )
                        }
                    }
                }

                //despues de crear o editar, cierra el bottomsheet
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = enabledButton
        ) {
            Text(text = stringResource(id = R.string.guardar))
        }
        Spacer(modifier = Modifier.padding(10.dp))
    }
}