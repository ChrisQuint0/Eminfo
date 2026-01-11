package com.eminfo.app.data.local.dao

import androidx.room.*
import com.eminfo.app.data.local.entities.EmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts ORDER BY isPrimary DESC, displayOrder ASC")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE isPrimary = 1 LIMIT 1")
    suspend fun getPrimaryContact(): EmergencyContact?

    @Query("SELECT * FROM emergency_contacts WHERE includeInQR = 1 ORDER BY isPrimary DESC, displayOrder ASC")
    suspend fun getQRContacts(): List<EmergencyContact>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: EmergencyContact): Long

    @Update
    suspend fun update(contact: EmergencyContact)

    @Delete
    suspend fun delete(contact: EmergencyContact)

    @Query("DELETE FROM emergency_contacts")
    suspend fun deleteAll()

    @Query("UPDATE emergency_contacts SET isPrimary = 0")
    suspend fun clearPrimaryFlags()
}