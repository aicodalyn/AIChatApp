package com.aichat.app.data.repository

import com.aichat.app.data.local.db.dao.ProviderProfileDao
import com.aichat.app.data.mapper.toDomain
import com.aichat.app.data.mapper.toEntity
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProviderRepositoryImpl @Inject constructor(
    private val dao: ProviderProfileDao,
) : ProviderRepository {

    override fun getAll(): Flow<List<ProviderProfile>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getEnabled(): Flow<List<ProviderProfile>> =
        dao.getEnabled().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: String): ProviderProfile? =
        dao.getById(id)?.toDomain()

    override suspend fun create(profile: ProviderProfile) =
        dao.insert(profile.toEntity())

    override suspend fun update(profile: ProviderProfile) =
        dao.update(profile.toEntity())

    override suspend fun delete(id: String) = dao.deleteById(id)

    override suspend fun toggleEnabled(id: String) = dao.toggleEnabled(id)

    override suspend fun reorder(profiles: List<ProviderProfile>) {
        profiles.forEachIndexed { index, profile ->
            dao.update(profile.copy(ordinal = index).toEntity())
        }
    }
}
