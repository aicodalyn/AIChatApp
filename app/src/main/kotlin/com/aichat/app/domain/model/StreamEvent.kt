package com.aichat.app.domain.model

data class StreamEvent(
    val delta: String = "",
    val finishReason: String? = null,
    val isDone: Boolean = false,
    val error: ProviderException? = null,
    val usage: UsageInfo? = null,
)

data class UsageInfo(
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val totalTokens: Int = 0,
)

class ProviderException(
    val providerType: ProviderType,
    val statusCode: Int? = null,
    override val message: String,
    val isRetryable: Boolean = false,
) : Exception(message)
