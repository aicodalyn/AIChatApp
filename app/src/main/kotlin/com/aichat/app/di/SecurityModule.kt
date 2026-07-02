package com.aichat.app.di

import android.content.Context
import com.aichat.app.data.local.datastore.SettingsDataStore
import com.aichat.app.data.local.security.BiometricManager
import com.aichat.app.data.local.security.EncryptedPreferences
import com.aichat.app.data.local.security.PinManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideEncryptedPreferences(@ApplicationContext context: Context): EncryptedPreferences =
        EncryptedPreferences(context)

    @Provides
    @Singleton
    fun providePinManager(@ApplicationContext context: Context): PinManager =
        PinManager(context)

    @Provides
    @Singleton
    fun provideBiometricManager(): BiometricManager = BiometricManager()

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
        SettingsDataStore(context)
}
