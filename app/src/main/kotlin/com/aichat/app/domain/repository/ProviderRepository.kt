package com.aichat.app.domain.repository

import com.aichat.app.domain.model.ProviderProfile
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {
    fun getAll(): Flow<List<ProviderProfile>>
    fun getEnabled(): Flow<List<ProviderProfile>>
    suspend fun getById(id: String): ProviderProfile?
    suspend fun create(profile: ProviderProfile)
    suspend fun update(profile: ProviderProfile)
    suspend fun delete(id: String)
    suspend fun toggleEnabled(id: String)
    suspend fun reorder(profiles: List<ProviderProfile>)
}
