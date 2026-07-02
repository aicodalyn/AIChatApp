package com.aichat.app.domain.model

data class ProviderCapabilities(
    val supportsStreaming: Boolean = true,
    val supportsVision: Boolean = false,
    val supportsSystemPrompt: Boolean = true,
    val supportsFunctionCalling: Boolean = false,
    val supportsJsonMode: Boolean = false,
    val supportsTemperature: Boolean = true,
    val supportsImageGeneration: Boolean = false,
    val supportsAudio: Boolean = false,
    val supportsEmbeddings: Boolean = false,
    val supportsReasoningModels: Boolean = false,
    val maxContextTokens: Int = 128_000,
    val supportedFileTypes: Set<String> = emptySet(),
    val needsApiKey: Boolean = true,
    val supportsModelListing: Boolean = true,
    val supportsStopSequences: Boolean = true,
    val supportsFrequencyPenalty: Boolean = true,
    val supportsPresencePenalty: Boolean = true,
    val supportsTopP: Boolean = true,
)
