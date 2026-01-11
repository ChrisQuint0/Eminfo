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