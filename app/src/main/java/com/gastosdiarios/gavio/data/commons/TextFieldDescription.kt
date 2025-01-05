package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization

@Composable
fun TextFieldDescription(
    description: String,
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = description,
        onValueChange = { onTextChanged(it) },
        modifier = modifier,
        singleLine = true,
        maxLines = 1,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray
        )
    ,
        //para mostrar la primer letra de la palabra en mayuscula
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
    )
}