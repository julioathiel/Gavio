package com.gastosdiarios.gavio.presentation.welcome.initial

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun InitialScreen(
    viewModel: InitialViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    val sizeLogo = 130.dp
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF202d3c),
                        Color.Black
                    ),
                    startY = 0f,
                    endY = 600f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(sizeLogo)
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icono dentro del círculo
            Image(
                painter = painterResource(id = R.drawable.logo_limitday),
                contentDescription = null,
                modifier = Modifier.size(sizeLogo),
                alignment = Alignment.Center
            )
        }
        Spacer(modifier = Modifier.size(100.dp))
        Text(
            text = "Gavio",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Controla tus gastos",
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.weight(1f))
        BoxButtons(
            viewModel,
            context,
            navigateToRegister,
            navigateToLogin,
            navigateToHomeScreen
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun BoxButtons(
    viewModel: InitialViewModel,
    context: Context,
    navigateToRegister: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = { navigateToRegister() }, modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
    ) {
        Text(text = "Registrarte gratis")
    }
    Spacer(modifier = Modifier.padding(4.dp))

    CustomButton(
        {
//            viewModel.signInWithGoogle(context = context) { success ->
//                if (success) {
//                    navigateToHomeScreen()
//                }
//            }
            doGoogleSignIn(
                viewModel,
                coroutineScope,
                context
            ) { success ->
                if (success) {
                    navigateToHomeScreen()
                }
            }
        },
        painterResource(id = R.drawable.ic_google),
        title = "Continuar con Goolge"
    )
    Spacer(modifier = Modifier.padding(4.dp))
    CustomButton(
        { viewModel.eventHandler(EventHandlerlLogin.ContinuarConFacebok(true)) },
        painterResource(id = R.drawable.ic_facebook),
        title = "Continuar con Facebook"
    )
    Spacer(modifier = Modifier.padding(10.dp))
    TextButton(
        onClick = { navigateToLogin() },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 32.dp)
    ) {
        Text(text = "Iniciar sesión", color = Color.White)
    }
}


@Composable
fun CustomButton(onClick: () -> Unit, painter: Painter, title: String) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .height(48.dp)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
            Image(
                painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun doGoogleSignIn(
    viewModel: InitialViewModel,
    coroutineScope: CoroutineScope,
    context: Context,
    navigateToHomeScreen: (Boolean) -> Unit
) {
// 1
    val credentialManager = CredentialManager.create(context)

    // 2
    fun getSignInWithGoogleOption(context: Context): GetSignInWithGoogleOption {

        // 2.1
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
        // 2.2
        return GetSignInWithGoogleOption.Builder(context.getString(R.string.web_client_id))
            .setNonce(hashedNonce)
            .build()
    }

// 3
    val googleSignRequest: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(getSignInWithGoogleOption(context))
        .build()

    coroutineScope.launch {
        try {
// 4
            val result = credentialManager.getCredential(
                request = googleSignRequest,
                context = context,
            )
// 5
            viewModel.handleSignIn(result, navigateToHomeScreen)
        } catch (e: NoCredentialException) {
            e.printStackTrace()
        } catch (e: GetCredentialException) {
            e.printStackTrace()
        }
    }
}
