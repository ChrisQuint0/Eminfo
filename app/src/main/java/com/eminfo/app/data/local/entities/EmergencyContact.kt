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