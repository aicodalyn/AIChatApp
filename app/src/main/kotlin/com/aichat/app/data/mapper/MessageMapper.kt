package com.aichat.app.data.mapper

import com.aichat.app.data.local.db.entity.MessageEntity
import com.aichat.app.domain.model.FileAttachment
import com.aichat.app.domain.model.Message
import com.aichat.app.domain.model.MessageRole
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun MessageEntity.toDomain(): Message {
    val attachmentsType = object : TypeToken<List<FileAttachment>>() {}.type
    val attachments: List<FileAttachment> = try {
        gson.fromJson(attachmentsJson, attachmentsType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
    return Message(
        id = id,
        conversationId = conversationId,
        role = MessageRole.valueOf(role),
        content = content,
        timestamp = timestamp,
        attachments = attachments,
        providerUsed = providerUsed,
        modelUsed = modelUsed,
    )
}

fun Message.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    role = role.name,
    content = content,
    timestamp = timestamp,
    attachmentsJson = gson.toJson(attachments),
    providerUsed = providerUsed,
    modelUsed = modelUsed,
)
