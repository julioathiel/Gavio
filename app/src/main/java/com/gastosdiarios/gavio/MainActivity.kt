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
import com.gastosdiarios.gavio.domain.enums.ThemeMode
import com.gastosdiarios.gavio.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.navigation.MyAppContent
import com.gastosdiarios.gavio.presentation.configuration.ajustes_avanzados.AjustesViewModel
import com.gastosdiarios.gavio.ui.theme.GavioTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

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

//    override fun onDestroy() {
//        super.onDestroy()
//        networkReceiver.unregister()
//    }
}