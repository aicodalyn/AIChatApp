package com.aichat.app.domain.repository

import com.aichat.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun getByConversationId(conversationId: String): Flow<List<Message>>
    suspend fun insert(message: Message)
    suspend fun update(message: Message)
    suspend fun delete(messageId: String)
    suspend fun deleteAllByConversation(conversationId: String)
    suspend fun getLatestByConversation(conversationId: String): Message?
}
