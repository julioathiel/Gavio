package com.gastosdiarios.gavio.presentation.welcome.login.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.gastosdiarios.gavio.R

@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    emailError: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    onNext: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        singleLine = true,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { if (it.isFocused) keyboardController?.show() },
        value = value,
        onValueChange = { onNewValue(it) },
        keyboardOptions = keyboardOptions,
        isError = emailError,
        keyboardActions = KeyboardActions(onNext = { onNext() }),
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )

    LaunchedEffect(value) {
        if (value.isNotBlank() && value.contains("@") && value.contains(".")) { // Validación básica del correo electrónico
            focusRequester.requestFocus() // Solicitar el foco al campo de correo electrónico
            keyboardController?.show() // Mostrar el teclado si no está visible
        }
    }
}

@Composable
fun PasswordField(
    valuePassword: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
) {
    var passwordVisibility by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val icon = if (passwordVisibility) {
        painterResource(id = R.drawable.ic_visibility_off)
    } else {
        painterResource(id = R.drawable.ic_visibility)
    }
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
        value = valuePassword,
        onValueChange = { onNewValue(it) },
        keyboardOptions = keyboardOptions,
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    painter = icon,
                    contentDescription = "show password",
                    tint = if (passwordVisibility || isFocused) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }, visualTransformation = if (passwordVisibility) {
            //si passwordVisibility es true...no hara nada, osea se muestra el texto
            VisualTransformation.None
        } else {
            //no se ve nada, pone puntitos ....
            PasswordVisualTransformation()
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun ButtonLogin(modifier: Modifier, title: String, onClick: () -> Unit, onEnabled: () -> Boolean) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        enabled = onEnabled(),
        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray)
    ) {
        Text(text = title)
    }
}