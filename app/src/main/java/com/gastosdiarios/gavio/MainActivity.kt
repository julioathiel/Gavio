package com.gastosdiarios.gavio

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.navigation.MyAppContent
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesViewModel
import com.gastosdiarios.gavio.presentation.configuration.notifications.NotificationsViewModel
import com.gastosdiarios.gavio.ui.theme.GavioTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val ajustesViewModel: AjustesViewModel by viewModels()
    private val notificationViewModel: NotificationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            notificationViewModel.notificationProgrammed()

            val uiState by ajustesViewModel.uiState.collectAsStateWithLifecycle()
            val isDarkModeSystem = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(isDarkModeSystem) }

                when (uiState) {
                    is UiStateSingle.Success -> {
                        val data = (uiState as UiStateSingle.Success<UserPreferences?>).data
                        isDarkMode = when (data?.themeMode) {
                            ThemeMode.MODE_AUTO -> isDarkModeSystem
                            ThemeMode.MODE_DAY -> false
                            ThemeMode.MODE_NIGHT -> true
                            null -> isDarkModeSystem
                        }
                    }
                    else -> {}
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
}