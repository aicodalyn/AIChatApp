package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.repository.ChatRepository
import com.aichat.app.domain.repository.MessageRepository
import com.aichat.app.data.local.security.EncryptedPreferences
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val encryptedPreferences: EncryptedPreferences,
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        profile: ProviderProfile,
        history: List<Message>,
    ): Flow<StreamEvent> {
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = MessageRole.USER,
            content = content,
            timestamp = System.currentTimeMillis(),
        )
        messageRepository.insert(userMessage)

        val apiKey = encryptedPreferences.getApiKey(profile.apiKeyRef)
            ?: throw IllegalStateException("API key not found")

        val chatMessages = history.map { msg ->
            ChatMessage(
                role = msg.role,
                content = msg.content,
                images = msg.attachments.filter { it.isImage }.map { it.base64Data ?: it.uri },
            )
        } + ChatMessage(
            role = MessageRole.USER,
            content = content,
        )

        return chatRepository.sendMessage(chatMessages, profile, apiKey)
    }

    suspend fun saveAssistantMessage(
        conversationId: String,
        content: String,
        providerName: String,
        model: String,
    ) {
        val message = Message(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = content,
            timestamp = System.currentTimeMillis(),
            providerUsed = providerName,
            modelUsed = model,
        )
        messageRepository.insert(message)
    }
}
