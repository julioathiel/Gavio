package com.gastosdiarios.gavio.bar_graph_custom


import android.util.Log
import com.gastosdiarios.gavio.data.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.data.repository.repositoriesFirestrore.BarDataFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class CircularBuffer(
    private val capacity: Int,
    private val db: BarDataFirestore,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

) {
    private val tag = "CircularBuffer"
    private var barDataList = ArrayList<BarDataModel>() // Lista en memoria (búfer circular)

    init {
        loadDataFromFirestore()
    }


    private fun loadDataFromFirestore() {
        coroutineScope.launch {
            try {
                db.getFlow().collect { data ->
                    // Ordenar los datos por monthNumber (orden cronológico)
                    val sortedData = data.sortedBy { it.index }
                    // Limpiar la lista actual
                    barDataList.clear()
                    // Agregar los datos a la lista en memoria (búfer circular) en orden de inserción
                    barDataList.addAll(sortedData)

                    // Ajustar la capacidad del búfer si es necesario
                    adjustBufferCapacityIfNeeded()
                    Log.d(tag, "loadDataFromFirestore: $barDataList")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading data from Firestore: ${e.message}")
            }
        }
    }

    private fun adjustBufferCapacityIfNeeded() {
        if (barDataList.size > capacity) {
            // Eliminar el elemento más antiguo (el primero)
            val itemToRemove = barDataList.removeAt(0)
            // Eliminar el elemento de Firestore
            deleteBarGraph(itemToRemove)
        }
    }

    fun updateBarGraphItem(item: BarDataModel, listaGuardada: List<BarDataModel>) {
        coroutineScope.launch {
            val existingItem = listaGuardada.find { it.monthNumber == item.monthNumber }
            val uid = existingItem?.uid

            if (existingItem != null) {
                // Si el mes ya existe, actualiza el elemento
                val updatedItem =
                    existingItem.copy(uid = uid, value = item.value, money = item.money)
                updateBarGraph(updatedItem)
                // Actualizar el elemento en la lista en memoria
                val index = barDataList.indexOfFirst { it.monthNumber == item.monthNumber }
                if (index != -1) {
                    barDataList[index] = updatedItem
                }
            } else {
                // el mes no existe, crea un nuevo elemento
                getBarGraphList().collect { list ->
                    if (list.isNotEmpty() && item.monthNumber != null) {
                        //agrega unn numero mas grande al index ultimo elemento de la lista
                        val newIndex = list.maxOfOrNull { it.index ?: 0 }?.plus(1) ?: 0

                        if (newIndex > capacity) {
                            //se a superado la capacidad del buffer, elimina el primer elemento de la lista
                            val itemToRemove = list.minByOrNull { it.index ?: 0 }
                            if (itemToRemove != null) {
                                //elimina de la memoria temporal el elemento con el index minimo
                                // y de la base de datos
                                adjustBufferCapacityIfNeeded()

                                //luego de eliminar el primer elemento, actualiza los demas index para que el ultimo sea el numero 12
                                barDataList.forEachIndexed { index, barDataModel ->
                                    barDataModel.index = index + 1
                                    //actualiza en la base de datos
                                    updateBarGraph(barDataModel)
                                }
                            }
                        } else {
                            //aun no se a superado la capacidad del buffer
                            createBarGraph(item.copy(index = newIndex))
                            // Agregar el nuevo dato a la lista en memoria (búfer circular)
                            barDataList.add(item.copy(index = newIndex))
                        }
                    }
                }
            }
        }
    }


    private fun createBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.create(item) }
    }

    private fun updateBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.update(item) }
    }

    private fun deleteBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.delete(item) }
    }

     suspend fun getBarGraphList(): Flow<List<BarDataModel>> {
        return db.getFlow()
    }

    suspend fun getBarDataList(): List<BarDataModel> {
        delay(1.seconds)
        return barDataList
    }
}