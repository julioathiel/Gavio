package com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.EditDeleteAlertDialog
import com.gastosdiarios.gavio.domain.model.UserCreateCategoryModel
import com.gastosdiarios.gavio.presentation.configuration.categorias_creadas.CategoryActions

@Composable
fun ItemCategory(
    item: UserCreateCategoryModel,
    categoryActions: CategoryActions
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.categoryIcon!!.toInt()),
                contentDescription = null,
                modifier = Modifier.padding(start = 16.dp, end = 32.dp)
            )
            Text(
                text = item.categoryName!!,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_option),
                    contentDescription = null
                )
            }

        }


        if (showMenu) {
            EditDeleteAlertDialog(
                onDismiss = { showMenu = false },
                onEditClick = { categoryActions.onEditClick(item, iconSelect = item.categoryIcon!!.toInt()) },
                onDeleteClick = { categoryActions.onDeleteClick(item) }
            )
        }
    }
}