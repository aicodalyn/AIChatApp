package com.aichat.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aichat.app.data.local.db.dao.ConversationDao
import com.aichat.app.data.local.db.dao.MessageDao
import com.aichat.app.data.local.db.dao.ProviderProfileDao
import com.aichat.app.data.local.db.entity.ConversationEntity
import com.aichat.app.data.local.db.entity.MessageEntity
import com.aichat.app.data.local.db.entity.ProviderProfileEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        ProviderProfileEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun providerProfileDao(): ProviderProfileDao
}
