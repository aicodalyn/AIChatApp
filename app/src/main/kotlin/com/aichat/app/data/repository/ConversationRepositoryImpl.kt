package com.aichat.app.data.repository

import com.aichat.app.data.local.db.dao.ConversationDao
import com.aichat.app.data.mapper.toDomain
import com.aichat.app.data.mapper.toEntity
import com.aichat.app.domain.model.Conversation
import com.aichat.app.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val dao: ConversationDao,
) : ConversationRepository {

    override fun getAll(): Flow<List<Conversation>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getById(id: String): Flow<Conversation?> =
        dao.getById(id).map { it?.toDomain() }

    override fun search(query: String): Flow<List<Conversation>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override fun getPinned(): Flow<List<Conversation>> =
        dao.getPinned().map { list -> list.map { it.toDomain() } }

    override suspend fun create(title: String): Conversation {
        val now = System.currentTimeMillis()
        val conversation = Conversation(
            id = UUID.randomUUID().toString(),
            title = title,
            createdAt = now,
            updatedAt = now,
        )
        dao.insert(conversation.toEntity())
        return conversation
    }

    override suspend fun update(conversation: Conversation) {
        dao.update(conversation.copy(updatedAt = System.currentTimeMillis()).toEntity())
    }

    override suspend fun delete(id: String) = dao.deleteById(id)

    override suspend fun togglePin(id: String) = dao.togglePin(id)

    override suspend fun rename(id: String, newTitle: String) = dao.rename(id, newTitle)
}
