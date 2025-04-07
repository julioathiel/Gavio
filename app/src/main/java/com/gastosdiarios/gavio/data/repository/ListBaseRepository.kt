package com.gastosdiarios.gavio.data.repository

import kotlinx.coroutines.flow.Flow

interface ListBaseRepository<T> {
    suspend fun getFlow(): Flow<List<T>>
    suspend fun create(entity: T)
    suspend fun update(entity: T)
    suspend fun delete(entity: T)
    suspend fun deleteAll()
}