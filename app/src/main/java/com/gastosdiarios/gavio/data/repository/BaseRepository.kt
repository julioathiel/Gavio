package com.gastosdiarios.gavio.data.repository

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T> {
    suspend fun getFlow(): Flow<T>
    suspend fun createOrUpdate(entity: T)
    suspend fun delete()
}