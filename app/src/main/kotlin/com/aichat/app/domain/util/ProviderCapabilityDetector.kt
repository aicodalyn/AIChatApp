package com.aichat.app.domain.util

import com.aichat.app.domain.model.ProviderCapabilities
import com.aichat.app.domain.model.ProviderType
import com.aichat.app.domain.provider.ProviderRegistry

class ProviderCapabilityDetector(
    private val registry: ProviderRegistry,
) {
    fun getCapabilities(type: ProviderType): ProviderCapabilities {
        return try {
            registry.get(type).capabilities
        } catch (_: Exception) {
            ProviderCapabilities()
        }
    }

    fun supportsVision(type: ProviderType): Boolean = getCapabilities(type).supportsVision
    fun supportsStreaming(type: ProviderType): Boolean = getCapabilities(type).supportsStreaming
    fun supportsFileUpload(type: ProviderType, mimeType: String): Boolean {
        return getCapabilities(type).supportedFileTypes.contains(mimeType)
    }
}
