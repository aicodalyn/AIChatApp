package com.aichat.app.domain.repository

import com.aichat.app.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getAll(): Flow<List<Conversation>>
    fun getById(id: String): Flow<Conversation?>
    fun search(query: String): Flow<List<Conversation>>
    fun getPinned(): Flow<List<Conversation>>
    suspend fun create(title: String): Conversation
    suspend fun update(conversation: Conversation)
    suspend fun delete(id: String)
    suspend fun togglePin(id: String)
    suspend fun rename(id: String, newTitle: String)
}
