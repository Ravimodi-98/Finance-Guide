package com.example.financeguide.data.local.dao

import androidx.room.*
import com.example.financeguide.data.local.entity.PendingSyncEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingSyncDao {
    @Query("SELECT * FROM pending_sync_queue ORDER BY timestamp ASC")
    suspend fun getAllPending(): List<PendingSyncEntity>

    @Query("SELECT * FROM pending_sync_queue ORDER BY timestamp ASC")
    fun getAllPendingFlow(): Flow<List<PendingSyncEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: PendingSyncEntity): Long

    @Delete
    suspend fun delete(item: PendingSyncEntity)

    @Query("DELETE FROM pending_sync_queue WHERE localId = :localId")
    suspend fun deleteById(localId: Long)
}
