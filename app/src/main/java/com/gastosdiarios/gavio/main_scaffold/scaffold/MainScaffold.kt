package com.gastosdiarios.gavio.main_scaffold.scaffold

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.gastosdiarios.gavio.data.commons.CommonsToolbar
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
    val isShowSnackbar = remember { SnackbarHostState() }
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
                    diasRestantes = uiState.diasRestantes,
                    isShowSnackbar,
                    onDismiss = { viewModel.onShowDialogClickTransaction() }
                )
            }
        },

        snackbarHost = { SnackbarHost(hostState = isShowSnackbar) }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(innerPadding).fillMaxSize()
        ) { page ->
            screens[page].content(innerPadding) // Mostrar contenido dinámico
        }
    }
}