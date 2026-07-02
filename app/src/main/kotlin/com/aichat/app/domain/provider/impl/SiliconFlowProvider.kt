package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class SiliconFlowProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.SILICONFLOW
    override val displayName = "SiliconFlow"
    override val defaultBaseUrl = "https://api.siliconflow.cn/v1"
    override val defaultModel = "Qwen/Qwen2.5-7B-Instruct"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsEmbeddings = true,
        maxContextTokens = 32_000,
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsTopP = true,
    )
}
