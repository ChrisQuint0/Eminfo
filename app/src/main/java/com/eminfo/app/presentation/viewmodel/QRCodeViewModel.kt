package com.eminfo.app.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import com.eminfo.app.data.local.entities.QRCodeSettings
import com.eminfo.app.data.repository.EmergencyRepository
import com.eminfo.app.util.EmergencyQRData
import com.eminfo.app.util.QRCodeGenerator
import com.eminfo.app.util.QRContact
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QRCodeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmergencyRepository

    // QR Settings
    private val _settings = MutableStateFlow(QRCodeSettings())
    val settings: StateFlow<QRCodeSettings> = _settings.asStateFlow()

    // Generated QR Code
    private val _qrCodeBitmap = MutableStateFlow<Bitmap?>(null)
    val qrCodeBitmap: StateFlow<Bitmap?> = _qrCodeBitmap.asStateFlow()

    // Loading state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // QR Data preview
    private val _qrDataPreview = MutableStateFlow<String>("")
    val qrDataPreview: StateFlow<String> = _qrDataPreview.asStateFlow()

    init {
        val database = EmergencyInfoDatabase.getDatabase(application)
        repository = EmergencyRepository(
            profileDao = database.emergencyProfileDao(),
            contactDao = database.emergencyContactDao(),
            qrSettingsDao = database.qrCodeSettingsDao(),
            messageDao = database.emergencyMessageDao()
        )

        loadSettings()
        generateQRCode()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            repository.getQRSettings().collect { settings ->
                _settings.value = settings ?: QRCodeSettings()
                generateQRCode()
            }
        }
    }

    fun updateSetting(field: QRField, enabled: Boolean) {
        val current = _settings.value
        val updated = when (field) {
            QRField.NAME -> current.copy(includeName = enabled)
            QRField.BLOOD_TYPE -> current.copy(includeBloodType = enabled)
            QRField.ALLERGIES -> current.copy(includeAllergies = enabled)
            QRField.MEDICAL_CONDITIONS -> current.copy(includeMedicalConditions = enabled)
            QRField.MEDICATIONS -> current.copy(includeMedications = enabled)
            QRField.PRIMARY_CONTACT -> current.copy(includePrimaryContact = enabled)
            QRField.PHYSICIAN -> current.copy(includePhysician = enabled)
        }

        _settings.value = updated

        viewModelScope.launch {
            repository.saveQRSettings(updated.copy(lastGenerated = System.currentTimeMillis()))
            generateQRCode()
        }
    }

    fun generateQRCode() {
        viewModelScope.launch {
            _isGenerating.value = true

            try {
                val profile = repository.getProfileOnce()
                val primaryContact = repository.getPrimaryContact()
                val settings = _settings.value

                val qrData = EmergencyQRData(
                    name = if (settings.includeName) profile?.fullName ?: "" else "",
                    bloodType = if (settings.includeBloodType) profile?.bloodType ?: "" else "",
                    allergies = if (settings.includeAllergies) profile?.allergies ?: "" else "",
                    medicalConditions = if (settings.includeMedicalConditions) profile?.medicalConditions ?: "" else "",
                    medications = if (settings.includeMedications) profile?.currentMedications ?: "" else "",
                    primaryContact = if (settings.includePrimaryContact && primaryContact != null) {
                        QRContact(
                            name = primaryContact.name,
                            phone = primaryContact.phoneNumber,
                            relationship = primaryContact.relationship
                        )
                    } else null,
                    physicianName = if (settings.includePhysician) profile?.physicianName ?: "" else "",
                    physicianPhone = if (settings.includePhysician) profile?.physicianPhone ?: "" else ""
                )

                // Generate preview text
                _qrDataPreview.value = buildPreviewText(qrData)

                // Generate QR code
                val bitmap = QRCodeGenerator.generateQRCode(qrData, 512)
                _qrCodeBitmap.value = bitmap

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun buildPreviewText(data: EmergencyQRData): String {
        val parts = mutableListOf<String>()

        if (data.name.isNotBlank()) parts.add("Name: ${data.name}")
        if (data.bloodType.isNotBlank()) parts.add("Blood Type: ${data.bloodType}")
        if (data.allergies.isNotBlank()) parts.add("Allergies: ${data.allergies}")
        if (data.medicalConditions.isNotBlank()) parts.add("Conditions: ${data.medicalConditions}")
        if (data.medications.isNotBlank()) parts.add("Medications: ${data.medications}")
        if (data.primaryContact != null) {
            parts.add("Emergency Contact: ${data.primaryContact.name} (${data.primaryContact.relationship})")
            parts.add("Contact Phone: ${data.primaryContact.phone}")
        }
        if (data.physicianName.isNotBlank()) parts.add("Physician: ${data.physicianName}")
        if (data.physicianPhone.isNotBlank()) parts.add("Physician Phone: ${data.physicianPhone}")

        return if (parts.isEmpty()) "No data selected" else parts.joinToString("\n")
    }
}

enum class QRField {
    NAME,
    BLOOD_TYPE,
    ALLERGIES,
    MEDICAL_CONDITIONS,
    MEDICATIONS,
    PRIMARY_CONTACT,
    PHYSICIAN
}