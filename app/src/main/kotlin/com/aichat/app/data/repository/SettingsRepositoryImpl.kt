package com.aichat.app.data.repository

import com.aichat.app.data.local.datastore.SettingsDataStore
import com.aichat.app.data.local.security.PinManager
import com.aichat.app.domain.model.AppSettings
import com.aichat.app.domain.model.ThemeMode
import com.aichat.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore,
    private val pinManager: PinManager,
) : SettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.settings

    override suspend fun updateTheme(mode: ThemeMode) = dataStore.updateTheme(mode)

    override suspend fun setDynamicColor(enabled: Boolean) = dataStore.setDynamicColor(enabled)

    override suspend fun setBiometricEnabled(enabled: Boolean) = dataStore.setBiometricEnabled(enabled)

    override suspend fun setPinEnabled(enabled: Boolean) = dataStore.setPinEnabled(enabled)

    override suspend fun setPin(pin: String) = pinManager.setPin(pin)

    override suspend fun verifyPin(pin: String): Boolean = pinManager.verifyPin(pin)

    override suspend fun setAutoFailover(enabled: Boolean) = dataStore.setAutoFailover(enabled)
}
