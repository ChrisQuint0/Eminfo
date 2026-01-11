package com.eminfo.app.data.local.dao

import androidx.room.*
import com.eminfo.app.data.local.entities.EmergencyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyProfileDao {
    @Query("SELECT * FROM emergency_profile WHERE id = 1")
    fun getProfile(): Flow<EmergencyProfile?>

    @Query("SELECT * FROM emergency_profile WHERE id = 1")
    suspend fun getProfileOnce(): EmergencyProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: EmergencyProfile)

    @Query("DELETE FROM emergency_profile")
    suspend fun deleteProfile()
}