package com.aichat.app.di

import com.aichat.app.data.remote.streaming.StreamingStateManager
import com.aichat.app.domain.provider.ProviderRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    @Singleton
    fun provideProviderRegistry(): ProviderRegistry = ProviderRegistry()

    @Provides
    @Singleton
    fun provideStreamingStateManager(): StreamingStateManager = StreamingStateManager()
}
