// data/local/entities/EmergencyProfile.kt
package com.eminfo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_profile")
data class EmergencyProfile(
    @PrimaryKey val id: Int = 1, // Single profile
    val fullName: String = "",
    val dateOfBirth: String = "",
    val bloodType: String = "",
    val height: String = "",
    val weight: String = "",
    val medicalConditions: String = "",
    val allergies: String = "",
    val currentMedications: String = "",
    val physicianName: String = "",
    val physicianPhone: String = "",
    val insuranceProvider: String = "",
    val insurancePolicyNumber: String = "",
    val additionalNotes: String = "",
    val photoUri: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

// data/local/entities/EmergencyContact.kt
package com.eminfo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val email: String = "",
    val isPrimary: Boolean = false,
    val displayOrder: Int = 0,
    val includeInQR: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

// data/local/entities/QRCodeSettings.kt
package com.eminfo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_settings")
data class QRCodeSettings(
    @PrimaryKey val id: Int = 1,
    val includeName: Boolean = true,
    val includeBloodType: Boolean = true,
    val includeAllergies: Boolean = true,
    val includeMedicalConditions: Boolean = true,
    val includeMedications: Boolean = true,
    val includePrimaryContact: Boolean = true,
    val includePhysician: Boolean = false,
    val customMessage: String = "",
    val lastGenerated: Long = 0
)

// data/local/entities/EmergencyMessage.kt
package com.eminfo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_messages")
data class EmergencyMessage(
    @PrimaryKey val id: Int = 1,
    val messageTemplate: String = "Emergency. I need help at [location].",
    val includeLocation: Boolean = true,
    val autoSendEnabled: Boolean = false
)