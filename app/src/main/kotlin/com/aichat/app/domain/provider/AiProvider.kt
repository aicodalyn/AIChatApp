package com.aichat.app.domain.provider

import com.aichat.app.domain.model.ChatMessage
import com.aichat.app.domain.model.ModelInfo
import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.model.StreamEvent
import kotlinx.coroutines.flow.Flow

interface AiProvider {
    val type: ProviderType
    val displayName: String
    val capabilities: ProviderCapabilities
    val defaultBaseUrl: String
    val defaultModel: String

    fun sendMessage(
        messages: List<ChatMessage>,
        model: String,
        apiKey: String,
        baseUrl: String,
        temperature: Double = 0.7,
        maxTokens: Int? = null,
    ): Flow<StreamEvent>

    suspend fun getModels(
        apiKey: String,
        baseUrl: String,
    ): List<ModelInfo>

    suspend fun validateConnection(
        apiKey: String,
        baseUrl: String,
    ): Boolean
}
