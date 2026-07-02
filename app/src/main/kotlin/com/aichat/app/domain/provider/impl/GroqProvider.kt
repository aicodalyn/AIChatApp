package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class GroqProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.GROQ
    override val displayName = "Groq"
    override val defaultBaseUrl = "https://api.groq.com/openai"
    override val defaultModel = "llama-3.3-70b-versatile"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
        supportsTemperature = true,
        maxContextTokens = 128_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
