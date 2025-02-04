package com.gastosdiarios.gavio.data.repository

interface ListBaseRepository<T> {
    suspend fun get(): List<T>
    suspend fun create(entity: T)
    suspend fun update(entity: T)
    suspend fun delete(entity: T)
    suspend fun deleteAll()
}