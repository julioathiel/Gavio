package com.gastosdiarios.gavio.presentation.splash_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

            false -> {
                onToLoginInitScreen()
            }
        }
    }

    if (uiState.startDestination == null) {
        Box(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
        ) {
            ProfileIcon(
                drawableResource = R.drawable.ic_ahorro,
                description = "splashScreen",
                sizeBox = 60,
                sizeIcon = 40,
                modifier = Modifier.align(Alignment.Center),
                colorCircle = Color.Transparent,
                colorIcon = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Gavio",
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

