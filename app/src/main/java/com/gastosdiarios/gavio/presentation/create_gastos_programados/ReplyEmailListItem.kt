package com.gastosdiarios.gavio.presentation.create_gastos_programados

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.ReplyProfileImage
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel


@Composable
fun ReplyEmailListItem(item: GastosProgramadosModel) {
    var isSelected by remember { mutableStateOf(false) }
    Log.d("TAGG", "ReplyEmailListItem: $item")
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .semantics { selected = isSelected }
            .clickable {
                // navigateToDetail(email.id)
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = item.icon?.toInt()?: R.drawable.ic_info,
                    description = item.title ?: "",
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.title ?: "",
                    )
                    Text(
                        text = item.cash ?: "",
                    )
                }
                IconButton(
                    onClick = { isSelected = !isSelected },
                    modifier = Modifier
                        .clip(CircleShape)

                ) {
//                    Icon(
//                        imageVector = Icons.Default.StarBorder,
//                        contentDescription = stringResource(id = R.string.description_favorite),
//                    )
                    if (isSelected)
                        Icon(painter = painterResource(id = R.drawable.ic_notificacion), contentDescription = "favorite")
                    else
                    Icon(painter = painterResource(id = R.drawable.ic_notifications), contentDescription = "favorite" )
                }
            }

            Text(
                text = item.date ?: "",
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Text(
                text = item.subTitle ?: "",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}