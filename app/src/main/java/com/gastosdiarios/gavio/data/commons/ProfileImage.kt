package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
    sizeBox: Int = 40,
    boxRounded: Int = 1,
    sizeIcon: Int = 24,
    colorBackground: Color,
    colorIcon: Color
) {
    Box(
        modifier = modifier
            .size(sizeBox.dp)
            .background(
                color = colorBackground,
                shape = RoundedCornerShape(boxRounded.dp)
            ),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = drawableResource),
            contentDescription = description,
            modifier = Modifier.size(sizeIcon.dp),
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(colorIcon)
        )
    }
}
