package com.aichat.app.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager @Inject constructor(
    context: Context,
) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "aichat_pin_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun setPin(pin: String) {
        prefs.edit().putString(PIN_HASH_KEY, hashPin(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getString(PIN_HASH_KEY, null) ?: return false
        return stored == hashPin(pin)
    }

    fun hasPin(): Boolean = prefs.contains(PIN_HASH_KEY)

    fun clearPin() {
        prefs.edit().remove(PIN_HASH_KEY).apply()
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PIN_HASH_KEY = "pin_hash"
    }
}
