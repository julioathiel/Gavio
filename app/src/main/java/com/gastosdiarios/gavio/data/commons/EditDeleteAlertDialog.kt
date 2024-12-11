package com.gastosdiarios.gavio.data.commons

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gastosdiarios.gavio.R

//funcionpara editar y eliminar un item de la transaccion
@Composable
fun EditDeleteAlertDialog(
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(id = R.string.elige_una_opcion)) },
        text = { Text(text = stringResource(R.string.si_presionas_eliminar_no_se_podra_recuperar)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onEditClick()
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.editar))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDeleteClick()
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.eliminar))
            }
        }
    )
}