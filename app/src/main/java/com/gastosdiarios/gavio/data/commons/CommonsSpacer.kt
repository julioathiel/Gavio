package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun CommonsSpacer(width: Dp, height: Dp) {
    Spacer(modifier = Modifier.width(width).height(height))
}