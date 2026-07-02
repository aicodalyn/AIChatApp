package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.MessageRole
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.repository.ChatRepository
import com.aichat.app.data.local.security.EncryptedPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val encryptedPreferences: EncryptedPreferences,
) {
    operator fun invoke(
        messages: List<ChatMessage>,
        profile: ProviderProfile,
    ): Flow<StreamEvent> {
        val apiKey = encryptedPreferences.getApiKey(profile.apiKeyRef)
            ?: throw IllegalStateException("API key not found")
        return chatRepository.sendMessage(messages, profile, apiKey)
    }
}
