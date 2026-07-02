package com.aichat.app.domain.provider

import com.aichat.app.domain.model.ProviderProfile
import com.aichat.app.domain.model.ProviderType

class ProviderRegistry {
    private val providers = mutableMapOf<ProviderType, AiProvider>()

    fun register(provider: AiProvider) {
        providers[provider.type] = provider
    }

    fun get(type: ProviderType): AiProvider =
        providers[type] ?: throw IllegalArgumentException("Unknown provider: $type")

    fun getAll(): List<AiProvider> = providers.values.toList()

    fun getFailoverProvider(
        current: ProviderType,
        enabledProfiles: List<ProviderProfile>,
    ): ProviderProfile? {
        val enabled = enabledProfiles.filter { it.enabled }.sortedBy { it.ordinal }
        val currentIndex = enabled.indexOfFirst { it.type == current }
        if (currentIndex == -1) return null
        val nextIndex = (currentIndex + 1) % enabled.size
        return if (nextIndex != currentIndex) enabled[nextIndex] else null
    }
}
