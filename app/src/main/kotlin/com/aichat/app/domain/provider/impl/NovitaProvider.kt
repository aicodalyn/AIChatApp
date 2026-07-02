package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class NovitaProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.NOVITA
    override val displayName = "Novita AI"
    override val defaultBaseUrl = "https://api.novita.ai/v3/openai"
    override val defaultModel = "meta-llama/llama-3.1-70b-instruct"
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
