package com.aichat.app.domain.model

data class ProviderStatus(
    val available: Boolean = true,
    val latencyMs: Long = 0,
    val lastChecked: Long = 0,
    val errorMessage: String? = null,
)
