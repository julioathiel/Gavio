package com.gastosdiarios.gavio.bar_graph_custom

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.utils.DateUtils

@Composable
fun BarGraphConfigCustom(listBarGraph: List<BarDataModel>) {
    listBarGraph.map { BarDataModel(it.uid, it.value, it.money, it.monthNumber, it.index) }
    //Configuración del gráfico de barras
    val config = BarGraphConfig<BarDataModel>()
        .data(listBarGraph)
        .dataBar(
            selectedBarColor = MaterialTheme.colorScheme.primary,
            unSelectedBarColor = MaterialTheme.colorScheme.secondaryContainer,
            width = 60.dp,
            espacioEntreBarras = 8.dp
        )
        .height(altura = 200.dp)
        .etiqueta(textSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        .etiquetaContainer(
            visible = true,
            color = MaterialTheme.colorScheme.surfaceVariant,
            cornerRadius = 30f
        )
        .etiquetaTranslateY(y = -5f)
        .roundType(roundType = BarType.TOP_CURVED)
        .viewBarText(
            textSize = 30.sp,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            cornerRadius = 100f,
            paddingBottom = 16.dp,
            heigth = 80.dp
        )
        .lineasEjeGraph(
            visible = true,
            color = MaterialTheme.colorScheme.surfaceVariant,
            size = 1.dp,
            cantidadLineas = 5
        )
        .etiquetaEjeY(visible = true, color = MaterialTheme.colorScheme.onSurfaceVariant)
        .lineaBase(visible = true, color = MaterialTheme.colorScheme.surfaceVariant)
        .etiquetaEjeX(
            textSixe = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            rotate = 0f
        )
        .lineaVertical(
            lineaVerticalesVisible = true,
            color = MaterialTheme.colorScheme.surfaceVariant,
            witdh = 1.dp,
            heigth = 4.dp
        )
        .dataToValue { it.value ?: 0f }
        .dataToLabel { it.money ?: "0" }
        .dataToMonth { DateUtils.getMonthName(it.monthNumber ?: 0) }
        .build()
    BarGraph(config = config)
}

