package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class QwenProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.QWEN
    override val displayName = "Qwen APIs"
    override val defaultBaseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1"
    override val defaultModel = "qwen-max"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
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
