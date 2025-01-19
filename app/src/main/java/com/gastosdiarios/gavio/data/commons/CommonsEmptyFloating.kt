package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonsEmptyFloating(onClick: () -> Unit, modifier: Modifier = Modifier){
    Box {
        CommonsIsEmpty()
        FloatingActionButton(
            onClick = onClick,
            modifier = modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(10.dp)
        )
        { Icon(Icons.Default.Add, contentDescription = null) }
    }
}