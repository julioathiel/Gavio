package com.gastosdiarios.gavio.data.commons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.gastosdiarios.gavio.domain.model.Action

@Composable
fun ActionButtons(actions: List<Action>) {
    actions.forEach { action ->
        IconButton(onClick = action.onClick) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.contentDescription
            )
        }
    }
}
