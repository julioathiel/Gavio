package com.gastosdiarios.gavio.presentation.splash_screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon

@Composable
fun MySplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    splashScreen: SplashScreen,
    onToHomeScreen: () -> Unit,
    onToBiometricScreen: () -> Unit,
    onToLoginInitScreen: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    splashScreen.setKeepOnScreenCondition { uiState.isLoading }

    LaunchedEffect(
        key1 = uiState.isLoading,
        key2 = uiState.securityActivated
    ) {
        when (uiState.userRegistered) {
            true -> {
                if (!uiState.isLoading) {

                    if (uiState.securityActivated) {
                        onToBiometricScreen()
                    } else {
                        onToHomeScreen()
                    }
                }
            }

            false -> { onToLoginInitScreen() }
        }
    }

    if (uiState.startDestination == null) {
        ProfileIcon(
            drawableResource = R.drawable.ic_ahorro,
            description = "splashScreen",
            sizeBox = 48,
            colorCircle = Color.Transparent,
            colorIcon = MaterialTheme.colorScheme.primary
        )
    }
}