package com.aichat.app.data.remote.model

data class ModelsResponse(
    val data: List<ModelDto> = emptyList(),
)

data class ModelDto(
    val id: String,
    val name: String? = null,
    val owned_by: String? = null,
)
