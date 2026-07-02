package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class CerebrasProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.CEREBRAS
    override val displayName = "Cerebras"
    override val defaultBaseUrl = "https://api.cerebras.ai/v1"
    override val defaultModel = "llama-3.3-70b"
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
