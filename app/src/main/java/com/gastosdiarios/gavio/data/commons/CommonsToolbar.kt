package com.gastosdiarios.gavio.data.commons

import android.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonsToolbar(title: String, textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface, colors: androidx.compose.ui.graphics.Color) {
    TopAppBar(
        title = {
            Text(text = title, color = textColor)
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = colors)
    )
}