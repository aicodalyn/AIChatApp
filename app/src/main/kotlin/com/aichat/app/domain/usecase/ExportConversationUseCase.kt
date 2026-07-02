package com.aichat.app.domain.usecase

import com.aichat.app.domain.model.Conversation
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.model.ExportFormat
import com.google.gson.GsonBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportConversationUseCase @Inject constructor() {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    operator fun invoke(
        conversation: Conversation,
        messages: List<Message>,
        format: ExportFormat,
    ): String = when (format) {
        ExportFormat.JSON -> exportToJson(conversation, messages)
        ExportFormat.MARKDOWN -> exportToMarkdown(conversation, messages)
    }

    private fun exportToJson(conversation: Conversation, messages: List<Message>): String {
        val exportData = mapOf(
            "conversation" to mapOf(
                "id" to conversation.id,
                "title" to conversation.title,
                "createdAt" to conversation.createdAt,
                "updatedAt" to conversation.updatedAt,
            ),
            "messages" to messages.map { msg ->
                mapOf(
                    "role" to msg.role.name.lowercase(),
                    "content" to msg.content,
                    "timestamp" to msg.timestamp,
                    "providerUsed" to msg.providerUsed,
                    "modelUsed" to msg.modelUsed,
                )
            },
        )
        return gson.toJson(exportData)
    }

    private fun exportToMarkdown(conversation: Conversation, messages: List<Message>): String {
        val sb = StringBuilder()
        sb.appendLine("# ${conversation.title}")
        sb.appendLine()
        messages.forEach { msg ->
            val role = when (msg.role) {
                com.aichat.app.domain.model.MessageRole.USER -> "User"
                com.aichat.app.domain.model.MessageRole.ASSISTANT -> "Assistant"
                com.aichat.app.domain.model.MessageRole.SYSTEM -> "System"
                com.aichat.app.domain.model.MessageRole.TOOL -> "Tool"
            }
            sb.appendLine("## $role")
            sb.appendLine()
            sb.appendLine(msg.content)
            sb.appendLine()
            sb.appendLine("---")
            sb.appendLine()
        }
        return sb.toString()
    }
}
