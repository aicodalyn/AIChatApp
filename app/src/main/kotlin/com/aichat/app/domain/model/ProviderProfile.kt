package com.aichat.app.domain.model

data class ProviderProfile(
    val id: String,
    val name: String,
    val type: ProviderType,
    val apiKeyRef: String,
    val baseUrl: String,
    val model: String,
    val parameters: ProviderParameters = ProviderParameters(),
    val enabled: Boolean = true,
    val ordinal: Int = 0,
)

data class ProviderParameters(
    val temperature: Double = 0.7,
    val topP: Double? = null,
    val maxTokens: Int? = null,
    val systemPrompt: String? = null,
    val frequencyPenalty: Double? = null,
    val presencePenalty: Double? = null,
)
