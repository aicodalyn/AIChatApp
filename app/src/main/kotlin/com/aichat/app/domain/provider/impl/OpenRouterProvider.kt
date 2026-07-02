package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class OpenRouterProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.OPENROUTER
    override val displayName = "OpenRouter"
    override val defaultBaseUrl = "https://openrouter.ai/api"
    override val defaultModel = "openai/gpt-4o"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 200_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
