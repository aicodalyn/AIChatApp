package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class MoonshotProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.MOONSHOT
    override val displayName = "Moonshot AI"
    override val defaultBaseUrl = "https://api.moonshot.cn/v1"
    override val defaultModel = "moonshot-v1-8k"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = false,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
        supportsTemperature = true,
        maxContextTokens = 128_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
