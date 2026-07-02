package com.aichat.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provider_profiles")
data class ProviderProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val apiKeyRef: String,
    val baseUrl: String,
    val model: String,
    val temperature: Double = 0.7,
    val topP: Double? = null,
    val maxTokens: Int? = null,
    val systemPrompt: String? = null,
    val frequencyPenalty: Double? = null,
    val presencePenalty: Double? = null,
    val enabled: Boolean = true,
    val ordinal: Int = 0,
)
