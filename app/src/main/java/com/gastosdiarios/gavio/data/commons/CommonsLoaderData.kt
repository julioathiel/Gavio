package com.gastosdiarios.gavio.data.commons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
    fun CommonsLoaderData(modifier: Modifier, image: Int, repeat: Boolean) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(image))
        val iterations = if (repeat) LottieConstants.IterateForever else 1
        LottieAnimation(
            composition = composition,
            iterations = iterations,
            modifier = modifier
        )
    }