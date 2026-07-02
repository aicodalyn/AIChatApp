package com.aichat.app.data.remote.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class MessageDto(
    val role: String,
    val content: JsonElement? = null,
    @SerializedName("name") val name: String? = null,
)
