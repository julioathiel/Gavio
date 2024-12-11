package com.gastosdiarios.gavio.presentation.configuration.acerca_de

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AcercaDeViewModel @Inject constructor(private val packageManager: PackageManager) :
    ViewModel() {

    private val _versionName = mutableStateOf("")
    val versionName: State<String> = _versionName
    private val _title = mutableStateOf("")
    val title : State<String> = _title
    private val _appIcon = mutableStateOf<Drawable?>(null)
    val appIcon: State<Drawable?> = _appIcon

    private val packageName = "com.gastosdiarios.gavio"


    init {
        viewModelScope.launch {
            try {
                val pInfo = packageManager.getPackageInfo(packageName, 0)
                _versionName.value = pInfo.versionName.toString()
                _appIcon.value = pInfo.applicationInfo?.loadIcon(packageManager)
                _title.value = pInfo.applicationInfo?.loadLabel(packageManager).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                // Manejar la excepción
            }
        }
    }
}