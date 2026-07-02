package com.aichat.app.domain.repository

import com.aichat.app.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateTheme(mode: com.aichat.app.domain.model.ThemeMode)
    suspend fun setDynamicColor(enabled: Boolean)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setPinEnabled(enabled: Boolean)
    suspend fun setPin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
    suspend fun setAutoFailover(enabled: Boolean)
}
