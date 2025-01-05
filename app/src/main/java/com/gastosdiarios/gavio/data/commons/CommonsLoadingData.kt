package com.gastosdiarios.gavio.data.commons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gastosdiarios.gavio.R

@Composable
fun CommonsLoadingData(modifier : Modifier = Modifier) {
//    Dialog(onDismissRequest = { /*TODO*/ }) {
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//                .background(
//                    MaterialTheme.colorScheme.surfaceContainerHigh,
//                    shape = RoundedCornerShape(8.dp)
//                )
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                CircularProgressIndicator(modifier = Modifier.size(30.dp))
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    modifier = Modifier.padding(horizontal = 8.dp),
//                    text = stringResource(R.string.cargando_espere_por_favor),
//                    style = MaterialTheme.typography.bodySmall,
//                    textAlign = TextAlign.Center,
//                    color = MaterialTheme.colorScheme.outline
//                )
//            }
//        }
//    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = stringResource(id = R.string.empty_screen_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.empty_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}