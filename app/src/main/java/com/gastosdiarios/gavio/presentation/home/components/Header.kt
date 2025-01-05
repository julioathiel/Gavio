package com.gastosdiarios.gavio.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.presentation.home.HomeViewModel

@Composable
fun Header(viewModel: HomeViewModel) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val currentUser = viewModel.getCurrentUser()
        val context = LocalContext.current
//        currentUser?.let { user ->
//            user.photoUrl?.let {
//                Box(modifier = Modifier.size(30.dp)
//                    .border(1.dp, Color.LightGray, CircleShape)
//                )
//                {
//                    AsyncImage(
//                        modifier = Modifier
//                            .clip(CircleShape)
//                            .size(30.dp),
//                        model = ImageRequest.Builder(context).data(it)
//                            .crossfade(true).build(),
//                        contentDescription = "profile picture",
//                        contentScale = ContentScale.Crop
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.padding(4.dp))
//        Column(Modifier.fillMaxWidth()) {
//            val primerName = currentUser?.displayName?.split(" ")?.firstOrNull()
//            if (primerName.isNullOrEmpty()) Text(text = "Hola", color = MaterialTheme.colorScheme.onSurface)
//            else Text(text = "Hola, $primerName", color = MaterialTheme.colorScheme.onSurface)
//        }
    }
}