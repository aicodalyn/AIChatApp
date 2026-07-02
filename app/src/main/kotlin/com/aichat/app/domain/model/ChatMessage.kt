package com.aichat.app.domain.model

data class ChatMessage(
    val role: MessageRole,
    val content: String,
    val images: List<String> = emptyList(),
)

enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT,
    TOOL,
}
