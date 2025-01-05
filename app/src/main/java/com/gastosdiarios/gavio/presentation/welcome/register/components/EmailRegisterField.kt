package com.gastosdiarios.gavio.presentation.welcome.register.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.gastosdiarios.gavio.R

@Composable
fun EmailRegisterField(
    valueEmail: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    onNext: () -> Unit
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = valueEmail,
        onValueChange = { onNewValue(it) },
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White)
        //   isError = emailError,
    )
}

@Composable
fun PasswordRegisterField(
    valuePassword: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    val image = if (passwordVisibility) {
        painterResource(id = R.drawable.ic_visibility_off)
    } else {
        painterResource(id = R.drawable.ic_visibility)
    }

    OutlinedTextField(
        singleLine = true,
        modifier = modifier,
        value = valuePassword,
        onValueChange = { onNewValue(it) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {

            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Image(
                    painter = image,
                    contentDescription = "show password",
                 //   colorFilter = ColorFilter.tint(color = Color.Red)
                )
            }
        }, visualTransformation = if (passwordVisibility) {
            //si passwordVisibility es true...no hara nada, osea se muestra el texto
            VisualTransformation.None
        } else {
            //no se ve nada, pone puntitos ....
            PasswordVisualTransformation()
        },
        colors = TextFieldDefaults.colors(focusedContainerColor = Color.White)
    )
}

@Composable
fun ButtonRegister(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit,
    onEnabled: () -> Boolean
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        enabled = onEnabled(),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray)
    ) {
        Text(text = title)
    }
}