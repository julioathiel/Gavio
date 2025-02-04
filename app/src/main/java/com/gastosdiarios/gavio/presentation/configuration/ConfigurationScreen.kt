package com.gastosdiarios.gavio.presentation.configuration

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum
import com.gastosdiarios.gavio.data.domain.model.OpcionEliminarModel
import com.gastosdiarios.gavio.presentation.configuration.components.ListConf
import com.gastosdiarios.gavio.presentation.configuration.components.ShareSheetButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    modifier: Modifier = Modifier,
    viewModel: ConfigurationViewModel = hiltViewModel(),
    onToHomeScreen: () -> Unit,
    onToUserProfileScreen: () -> Unit,
    onToCategoriasGastosScreen: () -> Unit,
    onToCreateGastosProgramadosScreen: () -> Unit,
    onToActualizarMaximoFechaScreen: () -> Unit,
    onToRecordatorioScreen: () -> Unit,
    onToAcercaDeScreen: () -> Unit,
    onToAjustesScreen: () -> Unit,
    onToExportarDatosScreen: () -> Unit,
    onToCongratulationsScreen: () -> Unit
) {
    // Manejar el evento de retroceso
    BackHandler { onToHomeScreen() }

    // Estado para controlar si se está mostrando el diálogo
    val uiState by viewModel.configurationUiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()


    ListConf(modifier = modifier.fillMaxSize(),
        items = com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.entries,
        onItemClick = { item ->
            when (item) {
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.ELIMINAR_EDITAR_PERFIL -> onToUserProfileScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.CATEGORIASNUEVAS -> onToCategoriasGastosScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.CREATE_GASTOS_PROGRAMADOS -> onToCreateGastosProgramadosScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.UPDATEDATE -> onToActualizarMaximoFechaScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.RECORDATORIOS -> onToRecordatorioScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.RESET -> viewModel.setShowBottomSheet(true)
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.COMPARTIR -> viewModel.setShowShare(true)
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.ACERCADE -> onToAcercaDeScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.AJUSTES_AVANZADOS -> onToAjustesScreen()
                com.gastosdiarios.gavio.data.domain.enums.ItemConfigurationEnum.EXPORTAR_DATOS -> onToExportarDatosScreen()
            }
        }
    )



    if (uiState.showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setShowBottomSheet(false) },
            sheetState = sheetState,
            content = {
                ContentBottomSheetReset(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth(),
                    onToCongratulationsScreen,
                    onDismiss = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                viewModel.setShowBottomSheet(false)
                            }
                        }
                    },
                    onAccept = { viewModel.clearDatabase() },
                    opcionesEliminar = viewModel.opcionesEliminar,
                    onConfirm = {
                        //opciones que el usuario eligio a eliminar
                            selectedOptions ->
                        selectedOptions.forEach { option ->
                            option.action()
                        }
                    }
                )
            }
        )
    }

    // Mostrar el diálogo de compartir app cuando sea necesario
    if (uiState.showShareApp) {
        ShareSheetButton(uiState.sharedLink, onDissmiss = { viewModel.setShowShare(false) })
    }
}

@Composable
fun ContentBottomSheetReset(
    viewModel: ConfigurationViewModel,
    modifier: Modifier = Modifier,
    onToCongratulationsScreen: () -> Unit,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    opcionesEliminar: List<com.gastosdiarios.gavio.data.domain.model.OpcionEliminarModel>,
    onConfirm: (Set<com.gastosdiarios.gavio.data.domain.model.OpcionEliminarModel>) -> Unit
) {
    val uiState by viewModel.configurationUiState.collectAsState()
    var isRotate by remember { mutableStateOf(false) }
    var isActivated by remember { mutableStateOf(false) }
    var selectedOptions by remember { mutableStateOf(setOf<com.gastosdiarios.gavio.data.domain.model.OpcionEliminarModel>()) }

    LaunchedEffect(key1 = uiState.resetPending) {
        if (uiState.resetPending) {
            onDismiss() // cierra el bottomSheet
            onToCongratulationsScreen() //si esta vacia muestra pantalla de congratulacion
            viewModel.setResetPending(false) // lo vuelve a false para que no se muestre el onToCongratulationScreen()
        }
    }
    //Contenido del bottomsheet
    Column(modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.title_reiniciar),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(1f) // Occupy available space
            )
            //aparece cuando se apreto el boton para reiniciar
            if (isActivated) {
                ContinuousRotationIcon(
                    isRotate = isRotate,
                    icon = R.drawable.ic_refresh,
                    MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.size(8.dp))
        //Cuerpo dialogo
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            opcionesEliminar.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Toggle the checkbox state when the row is clicked
                            val isChecked = selectedOptions.contains(option)
                            val newSelections = selectedOptions.toMutableSet()
                            if (!isChecked) {
                                newSelections.add(option)
                            } else {
                                newSelections.remove(option)
                            }
                            selectedOptions = newSelections
                        }) {
                    Checkbox(
                        checked = selectedOptions.contains(option),
                        onCheckedChange = { isChecked ->
                            val newSelections = selectedOptions.toMutableSet()
                            if (isChecked) {
                                newSelections.add(option)
                            } else {
                                newSelections.remove(option)
                            }
                            selectedOptions = newSelections
                        }
                    )

                    Text(text = option.nombre, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.size(80.dp))

        Button(
            onClick = {
                isRotate = true
                isActivated = true
                onAccept()
                onConfirm(selectedOptions)
            }, modifier = modifier.height(51.dp)
        ) {
            Text(
                text = stringResource(id = android.R.string.ok)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        TextButton(
            onClick = { onDismiss() },
            modifier = modifier.height(51.dp)
        ) {
            Text(text = stringResource(id = android.R.string.cancel))
        }
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
fun ContinuousRotationIcon(isRotate: Boolean, icon: Int, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isRotate) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    val modifier = if (isRotate) {
        Modifier.rotate(rotationAngle)
    } else {
        Modifier
    }
    Image(
        modifier = modifier.rotate(rotationAngle),
        painter = painterResource(id = icon),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color)
    )
}