package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.Conversation
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.repository.ConversationRepository
import com.aichat.app.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ManageConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
) {
    fun getAll(): Flow<List<Conversation>> = conversationRepository.getAll()

    fun getById(id: String): Flow<Conversation?> = conversationRepository.getById(id)

    fun getMessages(conversationId: String): Flow<List<Message>> = messageRepository.getByConversationId(conversationId)

    fun search(query: String): Flow<List<Conversation>> = conversationRepository.search(query)

    fun getPinned(): Flow<List<Conversation>> = conversationRepository.getPinned()

    suspend fun create(title: String = "New Chat"): Conversation = conversationRepository.create(title)

    suspend fun update(conversation: Conversation) = conversationRepository.update(conversation)

    suspend fun delete(id: String) {
        messageRepository.deleteAllByConversation(id)
        conversationRepository.delete(id)
    }

    suspend fun togglePin(id: String) = conversationRepository.togglePin(id)

    suspend fun rename(id: String, newTitle: String) = conversationRepository.rename(id, newTitle)

    suspend fun autoRename(conversationId: String, firstMessage: String) {
        val title = if (firstMessage.length > 50) {
            firstMessage.take(50) + "..."
        } else {
            firstMessage
        }
        conversationRepository.rename(conversationId, title)
    }
}
