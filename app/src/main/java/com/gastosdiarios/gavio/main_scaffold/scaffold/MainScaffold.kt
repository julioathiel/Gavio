package com.gastosdiarios.gavio.main_scaffold.scaffold

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.data.commons.CommonsToolbar
import com.gastosdiarios.gavio.data.commons.SnackbarMessage
import com.gastosdiarios.gavio.main_scaffold.list_screen.createScreens
import com.gastosdiarios.gavio.presentation.home.HomeViewModel
import com.gastosdiarios.gavio.presentation.home.components.MyFAB
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(navController: NavHostController) {
    val screens = createScreens(navController) // Inicializar pantallas aquí
    var selected by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { screens.size })
    val coroutineScope = rememberCoroutineScope()

    val viewModel = hiltViewModel<HomeViewModel>()
    val uiState by viewModel.homeUiState.collectAsState()
    val snackbarMessage by viewModel.snackbarManager.messages.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = snackbarMessage) {
        if (snackbarMessage != null){
            snackbarMessage?.let{
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar((it as SnackbarMessage.StringSnackbar).message)
            }
        }
    }

    Scaffold(
        topBar = {
            when (pagerState.currentPage) {
                0 -> {
                    CommonsToolbar(
                        title = screens[pagerState.currentPage].nameToolbar,
                        textColor = MaterialTheme.colorScheme.primary,// Título dinámico
                        colors = MaterialTheme.colorScheme.surface //color de fondo
                    )
                }

                else -> {
                    CommonsToolbar(
                        title = screens[pagerState.currentPage].nameToolbar, // Título dinámico
                        colors = MaterialTheme.colorScheme.surface //color de fondo
                    )
                }

            }

        },
        bottomBar = {

            NavigationBar {
                screens.forEachIndexed { index, screen ->
                    selected = pagerState.currentPage == index

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.icon),
                                contentDescription = screen.nameToolbar
                            )
                        },
                        label = { Text(text = screen.name) },
                        selected = selected,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        }
                    )

                }

            }
        },
        floatingActionButton = {
            if (pagerState.currentPage == 0) { // Mostrar FAB solo en HomeScreen (página 0)
                MyFAB(
                    snackbarHostState,
                    diasRestantes = uiState.diasRestantes,
                    onDismiss = { viewModel.onShowDialogClickTransaction() }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            snackbar = { data ->
                Snackbar(data, containerColor = MaterialTheme.colorScheme.primary)

        }) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            if (page == 0) {
                // Pasar isShowSnackbar a ContentHomeScreen
                screens[page].content(innerPadding)
            } else {
                screens[page].content(innerPadding)
            }
        }
    }
}