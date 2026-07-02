package com.aichat.app.domain.repository

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.ModelInfo
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.StreamEvent
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun sendMessage(
        messages: List<ChatMessage>,
        profile: ProviderProfile,
        apiKey: String,
    ): Flow<StreamEvent>

    suspend fun getModels(profile: ProviderProfile, apiKey: String): List<ModelInfo>
    suspend fun validateConnection(profile: ProviderProfile, apiKey: String): Boolean
}
