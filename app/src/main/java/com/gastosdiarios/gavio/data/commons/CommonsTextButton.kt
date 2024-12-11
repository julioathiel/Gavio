package com.gastosdiarios.gavio.data.commons

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CommonsTextButton(modifier: Modifier, title: String, onClick: () -> Unit) {
    TextButton(modifier = modifier, onClick = { onClick() }) {
        Text(text = title)
    }
}