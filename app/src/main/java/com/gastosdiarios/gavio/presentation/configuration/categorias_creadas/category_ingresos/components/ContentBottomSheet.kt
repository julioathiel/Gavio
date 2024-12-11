package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.category_ingresos.CategoryIngresosViewModel

@Composable
fun ContentBottomSheet(
    onDismiss: () -> Unit,
    viewModel: CategoryIngresosViewModel
) {
    val title by viewModel.tituloBottomSheet.collectAsState("")
    val categorySelect by viewModel.selectedCategoryIngresos.collectAsState()
    val isEditSelect by viewModel.isEditarSeleccion


    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.selecciona_un_icono),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider()

        CategoryListIngresos(
            iconSelected = categorySelect,
        ) { icon ->
            viewModel.iconoSelecionadoIngresos(icon)
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(id = R.string.nuevo_titulo),
            style = MaterialTheme.typography.labelMedium
        )
        TextField(
            value = title,
            onValueChange = { viewModel.actualizarTituloIngresos(it) },
            modifier = Modifier.fillMaxWidth(),
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
                if (isEditSelect) {
                    viewModel.updateItemIngresos(
                        UserCreateCategoryModel(
                            categoryName = title,
                            categoryIcon = categorySelect!!.icon.toString()
                        )
                    )
                } else {
                    //para crear un item de gastos nuevo
                    viewModel.createNewCategoryIngresos(
                        UserCreateCategoryModel(
                            categoryName = title,
                            categoryIcon = categorySelect!!.icon.toString(),
                            categoryType = CategoryTypeEnum.GASTOS
                        )
                    )
                }
                onDismiss() // Cierra el bottom sheet después de guardar
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !(title.isEmpty() || categorySelect == null)
        ) {
            Text(text = stringResource(id = R.string.guardar))
        }
    }
}