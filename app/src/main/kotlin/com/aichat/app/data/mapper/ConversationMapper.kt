package com.aichat.app.data.mapper

import com.aichat.app.data.local.db.entity.ConversationEntity
import com.aichat.app.domain.model.Conversation

fun ConversationEntity.toDomain() = Conversation(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    pinned = pinned,
    activeProviderProfileId = activeProviderProfileId,
)

fun Conversation.toEntity() = ConversationEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    updatedAt = updatedAt,
    pinned = pinned,
    activeProviderProfileId = activeProviderProfileId,
)
