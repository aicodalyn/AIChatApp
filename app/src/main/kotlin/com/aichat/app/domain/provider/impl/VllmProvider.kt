package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class VllmProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.VLLM
    override val displayName = "vLLM"
    override val defaultBaseUrl = "http://localhost:8000/v1"
    override val defaultModel = "local-model"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsTemperature = true,
        maxContextTokens = 32_768,
        needsApiKey = false,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
