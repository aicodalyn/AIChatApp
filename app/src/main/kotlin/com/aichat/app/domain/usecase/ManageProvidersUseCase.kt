package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageProvidersUseCase @Inject constructor(
    private val providerRepository: ProviderRepository,
) {
    fun getAll(): Flow<List<ProviderProfile>> = providerRepository.getAll()

    fun getEnabled(): Flow<List<ProviderProfile>> = providerRepository.getEnabled()

    suspend fun getById(id: String): ProviderProfile? = providerRepository.getById(id)

    suspend fun create(profile: ProviderProfile) = providerRepository.create(profile)

    suspend fun update(profile: ProviderProfile) = providerRepository.update(profile)

    suspend fun delete(id: String) = providerRepository.delete(id)

    suspend fun toggleEnabled(id: String) = providerRepository.toggleEnabled(id)

    suspend fun reorder(profiles: List<ProviderProfile>) = providerRepository.reorder(profiles)
}
