package com.eminfo.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import com.eminfo.app.data.local.entities.EmergencyProfile
import com.eminfo.app.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmergencyRepository

    // UI State
    private val _profileState = MutableStateFlow<EmergencyProfile?>(null)
    val profileState: StateFlow<EmergencyProfile?> = _profileState.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    // Form validation errors
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    init {
        val database = EmergencyInfoDatabase.getDatabase(application)
        repository = EmergencyRepository(
            profileDao = database.emergencyProfileDao(),
            contactDao = database.emergencyContactDao(),
            qrSettingsDao = database.qrCodeSettingsDao(),
            messageDao = database.emergencyMessageDao()
        )

        // Load existing profile
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            repository.getProfile().collect { profile ->
                _profileState.value = profile ?: EmergencyProfile()
            }
        }
    }

    fun updateField(field: ProfileField, value: String) {
        val current = _profileState.value ?: EmergencyProfile()
        _profileState.value = when (field) {
            ProfileField.FULL_NAME -> current.copy(fullName = value)
            ProfileField.DATE_OF_BIRTH -> current.copy(dateOfBirth = value)
            ProfileField.BLOOD_TYPE -> current.copy(bloodType = value)
            ProfileField.HEIGHT -> current.copy(height = value)
            ProfileField.WEIGHT -> current.copy(weight = value)
            ProfileField.MEDICAL_CONDITIONS -> current.copy(medicalConditions = value)
            ProfileField.ALLERGIES -> current.copy(allergies = value)
            ProfileField.MEDICATIONS -> current.copy(currentMedications = value)
            ProfileField.PHYSICIAN_NAME -> current.copy(physicianName = value)
            ProfileField.PHYSICIAN_PHONE -> current.copy(physicianPhone = value)
            ProfileField.INSURANCE_PROVIDER -> current.copy(insuranceProvider = value)
            ProfileField.INSURANCE_POLICY -> current.copy(insurancePolicyNumber = value)
            ProfileField.ADDITIONAL_NOTES -> current.copy(additionalNotes = value)
        }
        // Clear validation error for this field
        clearFieldError(field)
    }

    private fun clearFieldError(field: ProfileField) {
        _validationErrors.value = _validationErrors.value.toMutableMap().apply {
            remove(field.name)
        }
    }

    fun saveProfile() {
        val profile = _profileState.value ?: return

        // Validate
        val errors = validateProfile(profile)
        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        viewModelScope.launch {
            try {
                _saveStatus.value = SaveStatus.Saving
                repository.saveProfile(profile.copy(lastUpdated = System.currentTimeMillis()))

                // Update widget
                try {
                    com.eminfo.app.widget.EmergencyWidget.updateWidget(
                        getApplication<Application>().applicationContext
                    )
                } catch (e: Exception) {
                    // Widget update failed, but profile saved successfully
                }

                _saveStatus.value = SaveStatus.Success

                // Reset to idle after 2 seconds
                kotlinx.coroutines.delay(2000)
                _saveStatus.value = SaveStatus.Idle
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Failed to save")
            }
        }
    }

    private fun validateProfile(profile: EmergencyProfile): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (profile.fullName.isBlank()) {
            errors[ProfileField.FULL_NAME.name] = "Name is required"
        }

        // Validate phone number format if provided
        if (profile.physicianPhone.isNotBlank() && !isValidPhoneNumber(profile.physicianPhone)) {
            errors[ProfileField.PHYSICIAN_PHONE.name] = "Invalid phone format"
        }

        return errors
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Remove common formatting characters
        val cleaned = phone.replace(Regex("[\\s()-]"), "")
        // Check if it's 10-15 digits
        return cleaned.matches(Regex("^\\d{10,15}$"))
    }

    fun resetSaveStatus() {
        _saveStatus.value = SaveStatus.Idle
    }
}

enum class ProfileField {
    FULL_NAME,
    DATE_OF_BIRTH,
    BLOOD_TYPE,
    HEIGHT,
    WEIGHT,
    MEDICAL_CONDITIONS,
    ALLERGIES,
    MEDICATIONS,
    PHYSICIAN_NAME,
    PHYSICIAN_PHONE,
    INSURANCE_PROVIDER,
    INSURANCE_POLICY,
    ADDITIONAL_NOTES
}

sealed class SaveStatus {
    object Idle : SaveStatus()
    object Saving : SaveStatus()
    object Success : SaveStatus()
    data class Error(val message: String) : SaveStatus()
}