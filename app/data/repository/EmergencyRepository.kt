// data/repository/EmergencyRepository.kt
package com.eminfo.app.data.repository

import com.eminfo.app.data.local.dao.*
import com.eminfo.app.data.local.entities.*
import kotlinx.coroutines.flow.Flow

class EmergencyRepository(
    private val profileDao: EmergencyProfileDao,
    private val contactDao: EmergencyContactDao,
    private val qrSettingsDao: QRCodeSettingsDao,
    private val messageDao: EmergencyMessageDao
) {

    // Profile operations
    fun getProfile(): Flow<EmergencyProfile?> = profileDao.getProfile()
    suspend fun getProfileOnce(): EmergencyProfile? = profileDao.getProfileOnce()
    suspend fun saveProfile(profile: EmergencyProfile) = profileDao.insertOrUpdate(profile)
    suspend fun deleteProfile() = profileDao.deleteProfile()

    // Contact operations
    fun getAllContacts(): Flow<List<EmergencyContact>> = contactDao.getAllContacts()
    suspend fun getPrimaryContact(): EmergencyContact? = contactDao.getPrimaryContact()
    suspend fun getQRContacts(): List<EmergencyContact> = contactDao.getQRContacts()

    suspend fun insertContact(contact: EmergencyContact): Long {
        if (contact.isPrimary) {
            contactDao.clearPrimaryFlags()
        }
        return contactDao.insert(contact)
    }

    suspend fun updateContact(contact: EmergencyContact) {
        if (contact.isPrimary) {
            contactDao.clearPrimaryFlags()
        }
        contactDao.update(contact)
    }

    suspend fun deleteContact(contact: EmergencyContact) = contactDao.delete(contact)
    suspend fun deleteAllContacts() = contactDao.deleteAll()

    // QR Settings operations
    fun getQRSettings(): Flow<QRCodeSettings?> = qrSettingsDao.getSettings()
    suspend fun getQRSettingsOnce(): QRCodeSettings? = qrSettingsDao.getSettingsOnce()
    suspend fun saveQRSettings(settings: QRCodeSettings) = qrSettingsDao.insertOrUpdate(settings)

    // Emergency Message operations
    fun getEmergencyMessage(): Flow<EmergencyMessage?> = messageDao.getMessage()
    suspend fun saveEmergencyMessage(message: EmergencyMessage) = messageDao.insertOrUpdate(message)

    // Backup/Restore helper
    suspend fun getAllData(): EmergencyBackupData {
        return EmergencyBackupData(
            profile = profileDao.getProfileOnce(),
            contacts = contactDao.getQRContacts(),
            qrSettings = qrSettingsDao.getSettingsOnce(),
            message = messageDao.getMessage()
        )
    }
}

// Data model for backup
data class EmergencyBackupData(
    val profile: EmergencyProfile?,
    val contacts: List<EmergencyContact>,
    val qrSettings: QRCodeSettings?,
    val message: Flow<EmergencyMessage?>
)