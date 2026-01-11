package com.eminfo.app.data.local.dao

import androidx.room.*
import com.eminfo.app.data.local.entities.EmergencyMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyMessageDao {
    @Query("SELECT * FROM emergency_messages WHERE id = 1")
    fun getMessage(): Flow<EmergencyMessage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(message: EmergencyMessage)
}