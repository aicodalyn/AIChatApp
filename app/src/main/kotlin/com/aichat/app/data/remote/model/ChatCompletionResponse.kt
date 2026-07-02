package com.aichat.app.data.remote.model

import com.google.gson.annotations.SerializedName

data class ChatCompletionResponse(
    val id: String? = null,
    val choices: List<Choice>? = null,
    val usage: UsageDto? = null,
)

data class Choice(
    val index: Int? = null,
    val message: MessageDto? = null,
    @SerializedName("finish_reason") val finishReason: String? = null,
)

data class UsageDto(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0,
)
