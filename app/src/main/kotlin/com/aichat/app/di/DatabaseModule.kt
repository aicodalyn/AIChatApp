package com.aichat.app.di

import android.content.Context
import androidx.room.Room
import com.aichat.app.data.local.db.AppDatabase
import com.aichat.app.data.local.db.dao.ConversationDao
import com.aichat.app.data.local.db.dao.MessageDao
import com.aichat.app.data.local.db.dao.ProviderProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "aichat.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()

    @Provides
    fun provideProviderProfileDao(db: AppDatabase): ProviderProfileDao = db.providerProfileDao()
}
