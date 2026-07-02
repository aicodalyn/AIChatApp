package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class MistralProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.MISTRAL
    override val displayName = "Mistral AI"
    override val defaultBaseUrl = "https://api.mistral.ai"
    override val defaultModel = "mistral-large-latest"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsEmbeddings = true,
        maxContextTokens = 128_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
