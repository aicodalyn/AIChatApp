package com.aichat.app.data.mapper

import com.aichat.app.data.local.db.entity.ProviderProfileEntity
import com.aichat.app.domain.model.ProviderParameters
import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.ProviderType

fun ProviderProfileEntity.toDomain() = ProviderProfile(
    id = id,
    name = name,
    type = ProviderType.valueOf(type),
    apiKeyRef = apiKeyRef,
    baseUrl = baseUrl,
    model = model,
    parameters = ProviderParameters(
        temperature = temperature,
        topP = topP,
        maxTokens = maxTokens,
        systemPrompt = systemPrompt,
        frequencyPenalty = frequencyPenalty,
        presencePenalty = presencePenalty,
    ),
    enabled = enabled,
    ordinal = ordinal,
)

fun ProviderProfile.toEntity() = ProviderProfileEntity(
    id = id,
    name = name,
    type = type.name,
    apiKeyRef = apiKeyRef,
    baseUrl = baseUrl,
    model = model,
    temperature = parameters.temperature,
    topP = parameters.topP,
    maxTokens = parameters.maxTokens,
    systemPrompt = parameters.systemPrompt,
    frequencyPenalty = parameters.frequencyPenalty,
    presencePenalty = parameters.presencePenalty,
    enabled = enabled,
    ordinal = ordinal,
)
