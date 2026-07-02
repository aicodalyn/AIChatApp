package com.aichat.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aichat.app.data.local.db.entity.ProviderProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderProfileDao {
    @Query("SELECT * FROM provider_profiles ORDER BY ordinal ASC")
    fun getAll(): Flow<List<ProviderProfileEntity>>

    @Query("SELECT * FROM provider_profiles WHERE enabled = 1 ORDER BY ordinal ASC")
    fun getEnabled(): Flow<List<ProviderProfileEntity>>

    @Query("SELECT * FROM provider_profiles WHERE id = :id")
    suspend fun getById(id: String): ProviderProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProviderProfileEntity)

    @Update
    suspend fun update(profile: ProviderProfileEntity)

    @Query("DELETE FROM provider_profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE provider_profiles SET enabled = NOT enabled WHERE id = :id")
    suspend fun toggleEnabled(id: String)
}
