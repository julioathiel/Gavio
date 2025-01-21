package com.gastosdiarios.gavio.presentation.create_gastos_programados

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.ReplyProfileImage
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyEmailListItem(
    item: GastosProgramadosModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }
            ),
        colors = CardDefaults.cardColors(containerColor =
        if(isSelected){
            Color.Green
        }else{
            MaterialTheme.colorScheme.surfaceContainer
        }
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ReplyProfileImage(
                    drawableResource = item.icon?.toInt() ?: R.drawable.ic_info,
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
                    onClick = {  },
                    modifier = Modifier
                        .clip(CircleShape)

                ) {
//                    Icon(
//                        imageVector = Icons.Default.StarBorder,
//                        contentDescription = stringResource(id = R.string.description_favorite),
//                    )
                    if (isSelected)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notificacion),
                            contentDescription = "favorite"
                        )
                    else
                        Icon(
                            painter = painterResource(id = R.drawable.ic_notifications),
                            contentDescription = "favorite"
                        )
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