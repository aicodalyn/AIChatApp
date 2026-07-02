package com.aichat.app.domain.model

data class Message(
    val id: String,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long,
    val attachments: List<FileAttachment> = emptyList(),
    val providerUsed: String? = null,
    val modelUsed: String? = null,
)
