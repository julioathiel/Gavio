package com.gastosdiarios.gavio.bar_graph_custom


import com.gastosdiarios.gavio.domain.model.modelFirebase.BarDataModel
import com.gastosdiarios.gavio.domain.repository.repositoriesFirestrore.BarDataFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CircularBuffer(
    private val capacity: Int,
    private val db: BarDataFirestore,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

) {
    private val tag = "CircularBuffer"

     suspend fun adjustBufferCapacityIfNeeded() {
        coroutineScope.launch {
            val currentList: List<BarDataModel> = db.get()
            if (currentList.size.plus(1) > capacity) {
                // Remueve los primeros itemsToRemove elementos
                val itemsToRemove = currentList.size.plus(1) - capacity
                val itemsToRemoveList = currentList.reversed().subList(0, itemsToRemove)
                itemsToRemoveList.forEach { item -> deleteBarGraph(item) }
            }
        }
    }

    fun updateBarGraphItem(item: BarDataModel, listaGuardada: List<BarDataModel>) {
        coroutineScope.launch {
            val existingItem = listaGuardada.find { it.month == item.month }
            val uid = existingItem?.uid

            if (existingItem != null) {
                val updatedItem =
                    existingItem.copy(uid = uid, value = item.value, money = item.money)
                updateBarGraph(updatedItem)
            }
        }
    }


     suspend fun createBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.create(item) }
    }

    private suspend fun updateBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.update(item) }
    }

    private suspend fun deleteBarGraph(item: BarDataModel) {
        coroutineScope.launch { db.delete(item) }
    }

    suspend fun getBarGraphList(): List<BarDataModel> {
        return db.get()
    }
}