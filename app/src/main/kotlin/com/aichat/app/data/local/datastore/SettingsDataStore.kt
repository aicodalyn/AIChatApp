package com.aichat.app.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aichat.app.domain.model.AppSettings
import com.aichat.app.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context,
) {
    private val dataStore get() = context.dataStore

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = ThemeMode.valueOf(prefs[THEME_MODE] ?: ThemeMode.SYSTEM.name),
            dynamicColor = prefs[DYNAMIC_COLOR] ?: true,
            biometricEnabled = prefs[BIOMETRIC_ENABLED] ?: false,
            pinEnabled = prefs[PIN_ENABLED] ?: false,
            defaultProviderProfileId = prefs[DEFAULT_PROVIDER_ID],
            autoFailoverEnabled = prefs[AUTO_FAILOVER] ?: false,
            maxRetries = prefs[MAX_RETRIES] ?: 3,
        )
    }

    suspend fun updateTheme(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLOR] = enabled }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setPinEnabled(enabled: Boolean) {
        dataStore.edit { it[PIN_ENABLED] = enabled }
    }

    suspend fun setDefaultProvider(id: String?) {
        dataStore.edit {
            if (id != null) it[DEFAULT_PROVIDER_ID] = id
            else it.remove(DEFAULT_PROVIDER_ID)
        }
    }

    suspend fun setAutoFailover(enabled: Boolean) {
        dataStore.edit { it[AUTO_FAILOVER] = enabled }
    }

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        private val DEFAULT_PROVIDER_ID = stringPreferencesKey("default_provider_id")
        private val AUTO_FAILOVER = booleanPreferencesKey("auto_failover")
        private val MAX_RETRIES = intPreferencesKey("max_retries")
    }
}
