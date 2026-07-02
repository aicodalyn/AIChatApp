package com.aichat.app.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPreferences @Inject constructor(
    context: Context,
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "aichat_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun storeApiKey(keyRef: String, apiKey: String) {
        sharedPreferences.edit().putString(keyRef, apiKey).apply()
    }

    fun getApiKey(keyRef: String): String? =
        sharedPreferences.getString(keyRef, null)

    fun deleteApiKey(keyRef: String) {
        sharedPreferences.edit().remove(keyRef).apply()
    }

    fun hasApiKey(keyRef: String): Boolean =
        sharedPreferences.contains(keyRef)
}
