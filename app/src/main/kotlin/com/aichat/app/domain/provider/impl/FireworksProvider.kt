package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class FireworksProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.FIREWORKS
    override val displayName = "Fireworks AI"
    override val defaultBaseUrl = "https://api.fireworks.ai/inference/v1"
    override val defaultModel = "accounts/fireworks/models/llama-v3p3-70b-instruct"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsEmbeddings = true,
        maxContextTokens = 131_072,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
