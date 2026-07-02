package com.aichat.app.data.repository

import com.aichat.app.data.local.db.dao.MessageDao
import com.aichat.app.data.mapper.toDomain
import com.aichat.app.data.mapper.toEntity
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val dao: MessageDao,
) : MessageRepository {

    override fun getByConversationId(conversationId: String): Flow<List<Message>> =
        dao.getByConversationId(conversationId).map { list -> list.map { it.toDomain() } }

    override suspend fun insert(message: Message) = dao.insert(message.toEntity())

    override suspend fun update(message: Message) = dao.update(message.toEntity())

    override suspend fun delete(messageId: String) = dao.deleteById(messageId)

    override suspend fun deleteAllByConversation(conversationId: String) =
        dao.deleteAllByConversation(conversationId)

    override suspend fun getLatestByConversation(conversationId: String): Message? =
        dao.getLatestByConversation(conversationId)?.toDomain()
}
