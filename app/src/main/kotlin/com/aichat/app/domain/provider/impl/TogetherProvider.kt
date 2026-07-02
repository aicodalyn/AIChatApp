package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class TogetherProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.TOGETHER
    override val displayName = "Together AI"
    override val defaultBaseUrl = "https://api.together.xyz"
    override val defaultModel = "meta-llama/Llama-3.3-70B-Instruct-Turbo"
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
