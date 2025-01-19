package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.gastosdiarios.gavio.presentation.home.showSnackbar
import kotlinx.coroutines.launch

@Composable
fun MyFAB(
    diasRestantes: Int,
    isShowSnackbar: SnackbarHostState,
    onDismiss: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        onClick = {
            if (diasRestantes == 0) {
                scope.launch {
                    showSnackbar(
                        isShowSnackbar,
                        message = "Selecciona una fecha primero"
                    )
                }
            } else { onDismiss() }
        },
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add"
        )
    }
}