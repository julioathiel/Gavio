package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.DateUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyListItem(
    item: GastosProgramadosModel,
    isSelected: Boolean,
    expandedItem: GastosProgramadosModel?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val cash = CurrencyUtils.formattedCurrency(item.cash?.toDouble() ?: 0.0)
    val date = item.date ?: ""
    val title = item.title ?: ""
    val subtitle = item.subTitle ?: ""
    val icon = item.icon?.toInt() ?: R.drawable.ic_info


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = { onLongClick() }
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 20.dp)
    ) {
        Box(
            Modifier.background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(10.dp)
            )
        ) {
            ProfileIcon(
                drawableResource = icon,
                description = title,
                modifier = Modifier.align(Alignment.Center),
                sizeBox = 60,
                sizeIcon = 40,
                colorCircle = Color.Transparent,
                colorIcon = MaterialTheme.colorScheme.onBackground
            )

            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clip(CircleShape)
                    .align(Alignment.BottomEnd)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Done, contentDescription = "selected",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(2.dp)
                    )

                }
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = title)

                Text(
                    text = if (subtitle.isEmpty()) "sin descripcion" else item.subTitle ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (expandedItem?.uid == item.uid) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = cash)
            }
            Text(
                text = DateUtils.converterFechaPersonalizada(date),
                modifier = Modifier.padding(bottom = 8.dp),
            )

        }
    }
}