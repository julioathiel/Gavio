package com.gastosdiarios.gavio.presentation.transaction.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gastosdiarios.gavio.R
import com.gastosdiarios.gavio.data.commons.ProfileIcon
import com.gastosdiarios.gavio.domain.enums.TipoTransaccion
import com.gastosdiarios.gavio.domain.model.modelFirebase.TransactionModel
import com.gastosdiarios.gavio.presentation.transaction.TransactionsViewModel
import com.gastosdiarios.gavio.utils.CurrencyUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemTransactions(
    item: TransactionModel,
    viewModel: TransactionsViewModel,
    isSelect: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val data by viewModel.dataList.collectAsState()
    val textColor = if (item.tipoTransaccion == TipoTransaccion.INGRESOS) {
        //si el usuario eligio ingreso, el color de los numeros sera verde
        colorResource(id = R.color.verdeDinero)
    } else MaterialTheme.colorScheme.onSurfaceVariant//sin color


    Row(modifier = Modifier.fillMaxWidth()
        .combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick() }
        )
        .background(
            if (isSelect) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
        .padding(start = 16.dp, end = 16.dp, top = 5.dp, bottom = 20.dp)
    ) {

        //contenedor de icono
        ProfileIcon(
            drawableResource = item.icon.orEmpty().toInt(),
            description = item.title.orEmpty(),
            modifier = Modifier.clip(CircleShape),
            sizeBox = 48,
            sizeIcon = 30,
            colorBackground = MaterialTheme.colorScheme.surfaceContainer,
            colorIcon = MaterialTheme.colorScheme.onSurface
        )

        //   Spacer(modifier = Modifier.padding(start = 16.dp))
        //contenedor de titulo y subtitulo
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(text = item.title ?: "")

                Text(
                    text = if (item.subTitle?.isEmpty() == true) "sin descripcion" else item.subTitle
                        ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = if (data.expandedItem?.uid == item.uid) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis
                )

            }

            Text(
                text = CurrencyUtils.formattedCurrency(item.cash?.toDouble()),
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )

        }
    }
}