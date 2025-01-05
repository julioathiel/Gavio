package com.gastosdiarios.gavio.bar_graph_custom

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.presentation.analisis_gastos.AnalisisGastosViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun BarGraphConfigCustom(viewModel: AnalisisGastosViewModel,calendar: Calendar = Calendar.getInstance()) {

    val listBarGraph by viewModel.listBarDataModel.collectAsState()
     listBarGraph.items.map { BarDataModel(it.uid,it.value, it.month,it.money) }
    val mesActual = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())


    //Configuración del gráfico de barras
    val config = BarGraphConfig<BarDataModel>()
        .data(listBarGraph.items.reversed())
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
        .dataToMonth { it.month ?: mesActual!! }
        .build()
    BarGraph(config = config)
}

