package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsEmptyFloating
import com.gastosdiarios.gavio.data.commons.CommonsLoadingData
import com.gastosdiarios.gavio.data.commons.CommonsLoadingScreen
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack
import com.gastosdiarios.gavio.data.ui_state.ListUiState
import com.gastosdiarios.gavio.domain.enums.CategoryTypeEnum
import com.gastosdiarios.gavio.domain.model.CategoriesModel
import com.gastosdiarios.gavio.domain.model.CategoryDefaultModel
import com.gastosdiarios.gavio.domain.model.CategoryGastos
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.bottomsheet_horizontal_pager.screens.listHorizontalPagerScreens
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ContentBottomSheetGastosProgramados
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.ContentListGastosprogramados
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components.DialogDelete
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGastosProgramadosScreen(
    viewModel: CreateGastosDefaultViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.gastosprogramadosUiStateUiState.collectAsState()
    val uiStateDefault by viewModel.uiStateDefault.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val statePullToRefresh = rememberPullToRefreshState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedIndex = remember { mutableIntStateOf(-1) }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val selectedIndices by viewModel.selectedIndices.collectAsState()
    BottomSheetScaffold(topBar = {
        TopAppBarOnBack(
            title = if (uiStateDefault.isActivated) "" else stringResource(R.string.toolbar_gastos_programados),
            containerColor = MaterialTheme.colorScheme.surface,
            onBack = { onBack() },
            actions = {
                if (uiStateDefault.isActivated) {
                    // Mostrar el icono de eliminar solo si hay un elemento seleccionado
                    //icono para eliminar los seleccionados
                    if (selectedIndices.size > 1) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete"
                            )
                        }
                    } else {
                        // Mostrar el icono de eliminar y editar solo si hay más de un elemento seleccionado
                        IconButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete"
                            )
                        }
                        IconButton(onClick = { viewModel.isEditItemTrue() }) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Back"
                            )
                        }
                    }
                    //icono para eliminar todos aunque no esten seleccionados
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        )
    },
        sheetMaxWidth = 0.dp,
        sheetContent = {
            if (uiStateDefault.onDismiss) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.onDismissSet(false) },
                    sheetState = sheetState
                ) {
                   ContentBottomSheetGastosProgramados(
                       item = GastosProgramadosModel(title = ""),
                       onDismiss = { viewModel.onDismissSet(false) },
                       categoryTypes = CategoryTypeEnum.GASTOS,
                       viewModel = viewModel
                   )
                }
            }
        },
        content = {
            PullToRefreshBox(
                state = statePullToRefresh,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                isRefreshing = isRefreshing.isRefreshing,
                onRefresh = { viewModel.refreshData(context) },
            ) {
                ContentGastosProgramados(
                    uiState,
                    viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 50.dp)
                )
            }
        })


    DialogDelete(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = {
            viewModel.deleteItem(uiState.items[selectedIndex.intValue])
            selectedIndex.intValue = -1

        }
    )
}


@Composable
fun ContentGastosProgramados(
    uiState: ListUiState<GastosProgramadosModel>,
    viewModel: CreateGastosDefaultViewModel,
    modifier: Modifier
) {
    val isLoading by viewModel.isLoading.collectAsState()

    when {
        isLoading -> CommonsLoadingScreen(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        )

        uiState.items.isEmpty() -> {
            CreateGastosProgramadosEmpty(
                viewModel,
                modifier.fillMaxSize()
            )
        }

        uiState.isUpdateItem -> {
            ContentListGastosprogramados(
                uiState,
                viewModel,
                modifier
            )
            CommonsLoadingData()
        }

        else -> {
            ContentListGastosprogramados(
                uiState,
                viewModel,
                modifier
            )
        }
    }

}

@Composable
fun CreateGastosProgramadosEmpty(viewModel: CreateGastosDefaultViewModel, modifier: Modifier) {
    CommonsEmptyFloating(
        onClick = {
            viewModel.onDismissSet(true)
        }, modifier = modifier
    )
}

