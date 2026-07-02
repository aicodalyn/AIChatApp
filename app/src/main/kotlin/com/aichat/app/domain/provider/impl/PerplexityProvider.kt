package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class PerplexityProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.PERPLEXITY
    override val displayName = "Perplexity"
    override val defaultBaseUrl = "https://api.perplexity.ai"
    override val defaultModel = "llama-3.1-sonar-large-128k-online"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsTemperature = true,
        maxContextTokens = 128_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
