// data/local/dao/EmergencyProfileDao.kt
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

// data/local/dao/EmergencyContactDao.kt
package com.emergencyinfo.app.data.local.dao

import androidx.room.*
import com.emergencyinfo.app.data.local.entities.EmergencyContact
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

// data/local/dao/QRCodeSettingsDao.kt
package com.emergencyinfo.app.data.local.dao

import androidx.room.*
import com.emergencyinfo.app.data.local.entities.QRCodeSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeSettingsDao {
    @Query("SELECT * FROM qr_settings WHERE id = 1")
    fun getSettings(): Flow<QRCodeSettings?>

    @Query("SELECT * FROM qr_settings WHERE id = 1")
    suspend fun getSettingsOnce(): QRCodeSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: QRCodeSettings)
}

// data/local/dao/EmergencyMessageDao.kt
package com.emergencyinfo.app.data.local.dao

import androidx.room.*
import com.emergencyinfo.app.data.local.entities.EmergencyMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyMessageDao {
    @Query("SELECT * FROM emergency_messages WHERE id = 1")
    fun getMessage(): Flow<EmergencyMessage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(message: EmergencyMessage)
}