package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class OpenAiCompatibleProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.OPENAI_COMPATIBLE
    override val displayName = "OpenAI-compatible"
    override val defaultBaseUrl = "http://localhost:8080"
    override val defaultModel = "default"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = false,
        supportsTemperature = true,
        maxContextTokens = 32_768,
        needsApiKey = false,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
