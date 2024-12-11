package com.gastosdiarios.gavio.data.commons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonsButton(modifier: Modifier, title: String, onClick: () -> Unit) {
    Button(modifier = modifier, onClick = { onClick() }) {
        Text(text = title)
    }
}