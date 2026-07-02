package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class LiteLlmProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.LITELLM
    override val displayName = "LiteLLM"
    override val defaultBaseUrl = "http://localhost:4000"
    override val defaultModel = "gpt-4"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 128_000,
        needsApiKey = false,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
