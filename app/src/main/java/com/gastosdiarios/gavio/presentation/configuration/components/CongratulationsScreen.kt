package com.gastosdiarios.gavio.presentation.configuration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.CommonsButton
import com.gastosdiarios.gavio.data.commons.CommonsLoaderData
import com.gastosdiarios.gavio.presentation.configuration.ConfigurationViewModel

@Composable
fun CongratulationsScreen(viewModel: ConfigurationViewModel = hiltViewModel(), onToHomeScreen: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(100.dp))
        CommonsLoaderData(
            modifier = Modifier
                .fillMaxWidth()
                .size(300.dp),
            image = R.raw.congratulation_lottie,
            repeat = false
        )
        Text(
            text = stringResource(R.string.felicitaciones),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(text = stringResource(R.string.reinicio_exitoso),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        CommonsButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            title = stringResource(R.string.ir_a_inicio)
        ) {
            viewModel.setResetComplete(false)
            onToHomeScreen()
        }
        Spacer(Modifier.padding(20.dp))
    }
}