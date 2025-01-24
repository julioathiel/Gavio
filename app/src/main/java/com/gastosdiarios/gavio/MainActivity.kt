package com.gastosdiarios.gavio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import com.gastosdiarios.gavio.App.ConnectivityStatus.networkReceiver
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.navigation.MyAppContent
import com.gastosdiarios.gavio.navigation.SplashScreens
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
            val isDarkMode = when (option.themeMode) {
                ThemeMode.MODE_AUTO -> isSystemInDarkTheme()
                ThemeMode.MODE_DAY -> false
                ThemeMode.MODE_NIGHT -> true
                null -> isSystemInDarkTheme()
            }


            LaunchedEffect(key1 = Unit) {
                snapshotFlow { lifecycle.currentState }
                    .collect { state ->
                        when (state) {
                            Lifecycle.State.RESUMED -> {
                                // Update isInternet when the activity resumes
//                                viewModel.checkInternetConnection(mainActivity)
                                SplashScreens
                            }

                            Lifecycle.State.STARTED -> {
                                // Perform any necessary actions when the activity starts
                            }
                            // Handle other lifecycle events as needed
                            Lifecycle.State.DESTROYED -> {}
                            Lifecycle.State.INITIALIZED -> {}
                            Lifecycle.State.CREATED -> {}
                        }
                    }
            }
            GavioTheme(
                darkTheme = isDarkMode, dynamicColor = false
            ) {
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