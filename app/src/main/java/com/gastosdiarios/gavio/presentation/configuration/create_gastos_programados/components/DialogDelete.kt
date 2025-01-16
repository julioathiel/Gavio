package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DialogDelete(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar este elemento?") },
            confirmButton = {
                Button(onClick = {
                    onConfirm()
                    onDismiss() // Dismiss the dialog after confirmation
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
               TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}