package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class HyperbolicProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.HYPERBOLIC
    override val displayName = "Hyperbolic"
    override val defaultBaseUrl = "https://api.hyperbolic.xyz/v1"
    override val defaultModel = "meta-llama/Meta-Llama-3.1-70B-Instruct"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsEmbeddings = true,
        maxContextTokens = 128_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
