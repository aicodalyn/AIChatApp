package com.aichat.app.di

import com.aichat.app.data.repository.ChatRepositoryImpl
import com.aichat.app.data.repository.ConversationRepositoryImpl
import com.aichat.app.data.repository.MessageRepositoryImpl
import com.aichat.app.data.repository.ProviderRepositoryImpl
import com.aichat.app.data.repository.SettingsRepositoryImpl
import com.aichat.app.domain.repository.ChatRepository
import com.aichat.app.domain.repository.ConversationRepository
import com.aichat.app.domain.repository.MessageRepository
import com.aichat.app.domain.repository.ProviderRepository
import com.aichat.app.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindProviderRepository(impl: ProviderRepositoryImpl): ProviderRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
