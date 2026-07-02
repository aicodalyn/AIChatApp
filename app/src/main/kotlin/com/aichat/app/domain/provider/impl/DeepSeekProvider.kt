package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class DeepSeekProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.DEEPSEEK
    override val displayName = "DeepSeek"
    override val defaultBaseUrl = "https://api.deepseek.com"
    override val defaultModel = "deepseek-chat"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
        supportsTemperature = true,
        supportsReasoningModels = true,
        maxContextTokens = 64_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
