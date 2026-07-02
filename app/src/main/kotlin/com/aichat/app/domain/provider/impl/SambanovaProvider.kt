package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class SambanovaProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.SAMBANOVA
    override val displayName = "SambaNova"
    override val defaultBaseUrl = "https://api.sambanova.ai/v1"
    override val defaultModel = "Meta-Llama-3.1-8B-Instruct"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsTemperature = true,
        maxContextTokens = 4_096,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
