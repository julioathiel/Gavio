package com.gastosdiarios.gavio.domain.repository

interface BaseRepository<T> {
    suspend fun get(): T?
    suspend fun createOrUpdate(entity: T)
    suspend fun delete()
}