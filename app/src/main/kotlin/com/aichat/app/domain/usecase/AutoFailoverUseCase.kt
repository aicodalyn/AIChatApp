package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.ProviderException
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.StreamEvent
import com.aichat.app.domain.provider.ProviderRegistry
import com.aichat.app.domain.repository.ChatRepository
import com.aichat.app.data.local.security.EncryptedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutoFailoverUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val providerRegistry: ProviderRegistry,
    private val encryptedPreferences: EncryptedPreferences,
) {
    suspend operator fun invoke(
        messages: List<ChatMessage>,
        currentProfile: ProviderProfile,
        enabledProfiles: List<ProviderProfile>,
        maxRetries: Int = 3,
    ): Flow<StreamEvent> = flow {
        var currentAttempt = 0
        var current = currentProfile

        while (currentAttempt <= maxRetries) {
            try {
                val apiKey = encryptedPreferences.getApiKey(current.apiKeyRef)
                    ?: throw ProviderException(
                        providerType = current.type,
                        message = "API key not found",
                        isRetryable = false,
                    )

                chatRepository.sendMessage(messages, current, apiKey)
                    .catch { e ->
                        if (e is ProviderException && e.isRetryable && currentAttempt < maxRetries) {
                            val nextProfile = providerRegistry.getFailoverProvider(current.type, enabledProfiles)
                            if (nextProfile != null) {
                                current = nextProfile
                                currentAttempt++
                                emit(StreamEvent(delta = "\n\n[Falling over to ${current.name}...]\n\n"))
                            } else {
                                throw e
                            }
                        } else {
                            throw e
                        }
                    }
                    .collect { event ->
                        emit(event)
                    }
                return@flow
            } catch (e: ProviderException) {
                if (e.isRetryable && currentAttempt < maxRetries) {
                    val nextProfile = providerRegistry.getFailoverProvider(current.type, enabledProfiles)
                    if (nextProfile != null) {
                        current = nextProfile
                        currentAttempt++
                        emit(StreamEvent(delta = "\n\n[Falling over to ${current.name}...]\n\n"))
                    } else {
                        throw e
                    }
                } else {
                    throw e
                }
            }
        }
    }
}
