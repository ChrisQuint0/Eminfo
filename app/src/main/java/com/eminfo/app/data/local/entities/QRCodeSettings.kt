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