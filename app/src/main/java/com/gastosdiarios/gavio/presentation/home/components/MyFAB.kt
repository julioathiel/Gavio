package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.padding(start = 20.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "add",
          //  tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
suspend fun showSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String
) {
    snackbarHostState.showSnackbar(
        message = message,
        actionLabel = null,
        duration = SnackbarDuration.Short
    )
}