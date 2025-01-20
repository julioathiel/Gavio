package com.gastosdiarios.gavio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gastosdiarios.gavio.App.ConnectivityStatus.networkReceiver
import com.gastosdiarios.gavio.domain.enums.ModeDarkThemeEnum
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.navigation.MyAppContent
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesViewModel
import com.gastosdiarios.gavio.ui.theme.GavioTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val ajustesViewModel: AjustesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val option: UserPreferences by ajustesViewModel.uiState.collectAsState()
            val mode = when (option.themeMode) {
                ModeDarkThemeEnum.MODE_AUTO -> isSystemInDarkTheme()
                ModeDarkThemeEnum.MODE_DAY -> false
                ModeDarkThemeEnum.MODE_NIGHT -> true
                null -> isSystemInDarkTheme()
            }

            GavioTheme(darkTheme = mode, dynamicColor = false) {
                MyAppContent(
                    mainActivity = this,
                    splashScreen = splashScreen
                )
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkReceiver.unregister()
    }
}