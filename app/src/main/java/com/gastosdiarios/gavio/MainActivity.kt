package com.gastosdiarios.gavio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gastosdiarios.gavio.data.domain.enums.ThemeMode
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.UserPreferences
import com.gastosdiarios.gavio.data.ui_state.UiStateSingle
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen() {
//
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//
//    val scope = rememberCoroutineScope()
//
//    val context = LocalContext.current
//
//    val isTimePickerVisible = remember {
//        mutableStateOf(false)
//    }
//    val timePickerState = rememberTimePickerState()
//    val format = remember {
//        SimpleDateFormat("hh:mm a", Locale.getDefault())
//    }
//    val timeInMillis = remember { mutableLongStateOf(0L) }
//
//    ModalBottomSheet(
//        sheetState = sheetState,
//        modifier = Modifier.width(Dp.Infinity), // Ocupa todo el ancho disponible
//        onDismissRequest = { /*TODO*/ },
//        content = {
//            Form(
//                time = format.format(timeInMillis),
//                onTimeClick = {
//                    isTimePickerVisible.value = true
//                }) { name, dosage, check ->
//                val reminder = Alarm(
//                    name, dosage, timeInMillis.toString(), isTaken = false,
//                    isRepeat = check
//                )
//              //  viewModel.insert(reminder)
//                //inserta en la base de datos
//                if (check) {
//                    setUpPeriodicAlarm(context, reminder)
//                } else {
//                    setUpAlarm(context, reminder)
//                }
//                scope.launch {
//                    sheetState.hide()
//                }
//            }
//        }
//    )
//
//    Scaffold(topBar = {
//        TopAppBar(title = { Text(text = "Medication Reminder") }, actions = {
//            IconButton(onClick = {
//                scope.launch { sheetState.show() }
//            }) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = null)
//            }
//        })
//    }) { paddingValues ->
//
//        if (isTimePickerVisible.value) {
//            Dialog(onDismissRequest = { /*TODO*/ }) {
//                Column {
//
//                    TimePicker(state = timePickerState)
//                    Row {
//                        Button(onClick = {
//                            isTimePickerVisible.value = isTimePickerVisible.value.not()
//                        }) {
//                            Text(text = "Cancel")
//                        }
//
//                        Button(onClick = {
//                            val calendar = Calendar.getInstance().apply {
//                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
//                                set(Calendar.MINUTE, timePickerState.minute)
//                            }
//                            timeInMillis.longValue = calendar.timeInMillis
//                            isTimePickerVisible.value = false
//                        }) {
//                            Text(text = "Confirm")
//                        }
//                    }
//
//                }
//            }
//        }
//
//        if (uiState.data.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .padding(paddingValues)
//                    .fillMaxSize(), contentAlignment = Alignment.Center
//            ) {
//                Text(text = "Nothing found")
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .padding(paddingValues)
//                    .fillMaxSize()
//            ) {
//                items(uiState.data) {
//                    Card(
//                        modifier = Modifier.padding(8.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = if (it.isTaken) Color.Green.copy(alpha = 0.3f) else Color.Red.copy(
//                                alpha = 0.3f
//                            )
//                        )
//                    ) {
//                        Row(
//                            modifier = Modifier.padding(8.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Column(modifier = Modifier.weight(1f)) {
//                                Text(text = it.name)
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(text = it.dosage)
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(text = format.format(it.timeInMillis))
//                            }
//
//                            if (it.isRepeat) {
//                                IconButton(onClick = {
//                                    cancelAlarm(context, it)
//                                    viewModel.update(it.copy(isTaken = true, isRepeat = false))
//                                }) {
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.ic_schedule),
//                                        contentDescription = null
//                                    )
//                                }
//                            }
//
//                            IconButton(onClick = {
//                                cancelAlarm(context, it)
//                                viewModel.delete(it)
//                            }) {
//                                Icon(
//                                    painter = painterResource(id = R.drawable.ic_delete),
//                                    contentDescription = null
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }
//
//}

@Composable
fun Form(time: String, onTimeClick: () -> Unit, onClick: (String, String, Boolean) -> Unit) {

    val name = remember { mutableStateOf("") }
    val dosage = remember { mutableStateOf("") }
    val isChecked = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(top = 24.dp, start = 12.dp, end = 12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(value = name.value, onValueChange = {
            name.value = it
        }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = dosage.value, onValueChange = {
            dosage.value = it
        }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = time, onValueChange = {
        }, modifier = Modifier
            .clickable { onTimeClick.invoke() }
            .fillMaxWidth(), enabled = false)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

            Text(text = "Cronograma")//Cronograma 0 shedule es lo mismo
            Spacer(modifier = Modifier.width(12.dp))
            Switch(checked = isChecked.value, onCheckedChange = {
                isChecked.value = it
            })
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick.invoke(name.value, dosage.value, isChecked.value) }) {
            Text(text = "Save")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

}