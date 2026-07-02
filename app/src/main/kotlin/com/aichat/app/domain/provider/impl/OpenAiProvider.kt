package com.aichat.app.domain.provider.impl

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.BaseOpenAiCompatibleProvider

class OpenAiProvider : BaseOpenAiCompatibleProvider() {
    override val type = ProviderType.OPENAI
    override val displayName = "OpenAI"
    override val defaultBaseUrl = "https://api.openai.com"
    override val defaultModel = "gpt-4o"
    override val capabilities = ProviderCapabilities(
        supportsStreaming = true,
        supportsVision = true,
        supportsSystemPrompt = true,
        supportsFunctionCalling = true,
        supportsJsonMode = true,
        supportsTemperature = true,
        supportsImageGeneration = true,
        supportsAudio = true,
        supportsEmbeddings = true,
        supportsReasoningModels = true,
        maxContextTokens = 128_000,
        supportedFileTypes = setOf("image/jpeg", "image/png", "image/webp", "image/gif", "application/pdf"),
        needsApiKey = true,
        supportsModelListing = true,
        supportsStopSequences = true,
        supportsFrequencyPenalty = true,
        supportsPresencePenalty = true,
        supportsTopP = true,
    )
}
