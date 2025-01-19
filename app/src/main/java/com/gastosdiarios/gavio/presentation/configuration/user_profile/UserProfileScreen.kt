package com.gastosdiarios.gavio.presentation.configuration.user_profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsTextButton
import com.gastosdiarios.gavio.data.commons.TopAppBarOnBack

@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onToLoginInitScreen: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBarOnBack(
                title = "Mi cuenta",
                containerColor = MaterialTheme.colorScheme.surface,
                onBack = { onBack() }
            )
        }

    ) {
        val currentUser = viewModel.getCurrenthUser()
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Foto de perfil
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .border(1.dp, Color.LightGray, CircleShape)
            ) {
                if (currentUser?.photoUrl != null) {
                    currentUser.photoUrl?.let { user ->

                        AsyncImage(
                            modifier = Modifier
                                .size(128.dp)
                                .clip(CircleShape),
                            model = ImageRequest.Builder(LocalContext.current).data(user)
                                .crossfade(true).build(),
                            contentDescription = "profile picture",
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_persona_circle), // Reemplazacon la imagen actual del usuario
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(128.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Nombre del usuario
            Text(text = "Usuario")
            currentUser?.displayName?.let { nameUser ->
                if (nameUser.isNotEmpty()) {
                    Text(
                        text = nameUser,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Text(
                        text = "desconocido",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            currentUser?.uid.let { uid ->
                if (uid != null) {
                    Text(
                        text = "uid : $uid",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            // Opciones del perfil
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            CommonsTextButton(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                title = stringResource(R.string.cerrar_sesion),
                onClick = {
                    viewModel.signOut()
                    onToLoginInitScreen()
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            CommonsTextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                title = stringResource(R.string.eliminar_cuenta)
            ) {
                showDialog = true
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.eliminar_cuenta)) },
            text = { Text(stringResource(R.string.estas_seguro_de_que_quieres_eliminar_tu_cuenta_esta_accion_no_se_puede_deshacer)) },
            confirmButton = {
                TextButton(onClick = {
                    // Lógica para eliminar la cuenta
                    viewModel.deleteUser()
                    showDialog = false
                    // Navega a la pantalla de inicio de sesión o realiza otras acciones necesarias
                    onBack()
                    Toast.makeText(
                        context,
                        context.getString(R.string.cuenta_eliminada),
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    Text(stringResource(id = R.string.eliminar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun UserProfileScreen() {

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Foto de perfil
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .border(1.dp, Color.LightGray, CircleShape)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_persona_circle), // Reemplazacon la imagen actual del usuario
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(128.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Nombre del usuario
                Text(
                    text = "julio",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(32.dp))

                // Opciones del perfil
                Spacer(modifier = Modifier.height(8.dp))


                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    var name by remember { mutableStateOf("") }

                    Text(text = "Editar nombre")
                    OutlinedTextField(
                        value = name, onValueChange = { newName ->
                            name = newName
                        }, modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    CommonsTextButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        title = stringResource(R.string.cerrar_sesion),
                        onClick = {})

                    CommonsTextButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp)
                            .background(MaterialTheme.colorScheme.error),
                        title = stringResource(R.string.eliminar_cuenta)
                    ) {
                        showDialog = true
                    }
                }

            }

        }
    }
}