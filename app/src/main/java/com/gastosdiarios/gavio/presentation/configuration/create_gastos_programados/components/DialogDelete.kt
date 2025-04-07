package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R

@Composable
fun DialogDelete(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    textContent:String
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(textContent) },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss()
                }) {
                    Text(stringResource(R.string.eliminar))
                }
            },
            dismissButton = {
               TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}