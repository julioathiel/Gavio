package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.CurrencyAmountInputVisualTransformation

@Composable
fun TextFielDineroMovimientos(
    cantidadIngresada: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        value = cantidadIngresada,
        onValueChange = { onTextChanged(it) },
        visualTransformation = CurrencyAmountInputVisualTransformation(
            fixedCursorAtTheEnd = true
        ), placeholder = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    stringResource(id = R.string._0_00),
                    color = colorResource(id = R.color.grayTres),
                    fontSize = 40.sp
                )
            }

        },
        singleLine = true,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 40.sp)
    )
}