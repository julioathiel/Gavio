package com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.GastosProgramadosModel
import com.gastosdiarios.gavio.presentation.configuration.create_gastos_programados.CreateGastosProgramadosViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils
import com.gastosdiarios.gavio.utils.DateUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReplyListItem(
    item: GastosProgramadosModel,
    isSelected: Boolean,
    viewModel: CreateGastosProgramadosViewModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val data by viewModel.dataList.collectAsState()
    val cash = CurrencyUtils.formattedCurrency(item.cash?.toDouble() ?: 0.0)
    val date = item.date ?: ""
    val title = item.title ?: ""
    val subtitle = item.subTitle ?: ""
    val icon = item.icon?.toInt() ?: R.drawable.ic_info
    val hour = item.hour ?: ""
    val minute = item.minute ?: ""


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = { onLongClick() }
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceBright
                } else {
                    MaterialTheme.colorScheme.surface
                },

                )
            .padding(start = 16.dp,end = 16.dp, top = 5.dp, bottom = 20.dp)
    ) {
        if (item.select == true) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp) // Ancho de la barra
                    .background(
                        Color.Green,
                        shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
                    ) // Color de la barra
            )
        } else {
            //linea que se pinta de color segun estado
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color.Transparent) // Color de la barra
            )
        }

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
                sizeIcon = 30,
                colorBackground = Color.Transparent,
                colorIcon = MaterialTheme.colorScheme.primary
            )

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    )
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

        Row(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = title)
                Text(
                    text = if (subtitle.isEmpty()) "sin descripcion" else item.subTitle ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (data.expandedItem?.uid == item.uid) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = cash)

            }
            Column {
                Text(
                    text = DateUtils.converterFechaPersonalizada(date),
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = "$hour:$minute",
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.End),
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun ReplyListItem(
) {
    val isSelected by remember { mutableStateOf(false) }
    val select by remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { }
            )
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.surfaceBright
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
            .padding(end = 16.dp, top = 5.dp, bottom = 20.dp)
    ) {
        if (select) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
                    .width(8.dp) // Ancho de la barra
                    .background(Color.Green) // Color de la barra
            )
        }

        Box(
            Modifier.background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(10.dp)
            )
        ) {

            ProfileIcon(
                drawableResource = R.drawable.ic_info,
                description = "title",
                modifier = Modifier.align(Alignment.Center),
                sizeBox = 60,
                sizeIcon = 30,
                colorBackground = Color.Transparent,
                colorIcon = MaterialTheme.colorScheme.primary
            )

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        shape = CircleShape
                    )
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
                Text(text = "title")

                Text(
                    text = "sin descripcion",
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis
                )

                Text(text = "$100.00")
            }
            Column {
                Text(
                    text = "27 jun.2024",
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = "$13:23",
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.End),
                )
            }
        }
    }
}