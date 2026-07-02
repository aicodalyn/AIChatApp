package com.aichat.app.domain.model

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val biometricEnabled: Boolean = false,
    val pinEnabled: Boolean = false,
    val defaultProviderProfileId: String? = null,
    val autoFailoverEnabled: Boolean = false,
    val maxRetries: Int = 3,
)

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM,
}
