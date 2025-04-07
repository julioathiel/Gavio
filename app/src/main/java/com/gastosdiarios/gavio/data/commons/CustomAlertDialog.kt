package com.gastosdiarios.gavio.data.commons

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAlertDialog(title: String, message: String, onDismiss: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismiss() }, content = {
        Text(text = title)
        Text(text = message)
    })
}