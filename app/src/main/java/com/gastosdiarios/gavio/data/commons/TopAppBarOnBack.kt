package com.gastosdiarios.gavio.data.commons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarOnBack(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color,
    actions: @Composable () -> Unit = {},
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                modifier = modifier,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
               Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
            }
        },
        actions = { actions() }
    )
}