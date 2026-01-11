package com.eminfo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_profile")
data class EmergencyProfile(
    @PrimaryKey val id: Int = 1,
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