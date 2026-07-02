package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class XaiProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.XAI
    override val displayName = "xAI (Grok)"
    override val defaultBaseUrl = "https://api.x.ai"
    override val defaultModel = "grok-2"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 131_072,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
