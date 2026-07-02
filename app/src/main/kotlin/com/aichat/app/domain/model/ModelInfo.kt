package com.aichat.app.domain.model

data class ModelInfo(
    val id: String,
    val name: String,
    val providerType: ProviderType,
    val contextLength: Int = 128_000,
    val capabilities: ProviderCapabilities = ProviderCapabilities(),
)
