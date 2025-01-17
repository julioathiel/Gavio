package com.gastosdiarios.gavio.presentation.transaction.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolbarTransactions() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.toolbar_registro_gastos)
            )
        }
    )
}