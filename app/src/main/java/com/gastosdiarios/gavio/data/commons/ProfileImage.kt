package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ProfileIcon(
    drawableResource: Int,
    description: String,
    modifier: Modifier = Modifier,
    sizeBox:Int,
    colorCircle:Color,
) {
    Box(
        modifier = modifier
            .size(sizeBox.dp)
            .background(color = colorCircle),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = description,
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(colorIcon)
        )
    }
}
