package com.eminfo.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import com.eminfo.app.data.local.entities.EmergencyContact
import com.eminfo.app.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmergencyRepository

    // Contacts list
    val contacts: StateFlow<List<EmergencyContact>>

    // Dialog state
    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    private val _editingContact = MutableStateFlow<EmergencyContact?>(null)
    val editingContact: StateFlow<EmergencyContact?> = _editingContact.asStateFlow()

    // Form state
    private val _contactName = MutableStateFlow("")
    val contactName: StateFlow<String> = _contactName.asStateFlow()

    private val _relationship = MutableStateFlow("")
    val relationship: StateFlow<String> = _relationship.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _isPrimary = MutableStateFlow(false)
    val isPrimary: StateFlow<Boolean> = _isPrimary.asStateFlow()

    private val _includeInQR = MutableStateFlow(true)
    val includeInQR: StateFlow<Boolean> = _includeInQR.asStateFlow()

    // Validation errors
    private val _validationErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val validationErrors: StateFlow<Map<String, String>> = _validationErrors.asStateFlow()

    // Save status
    private val _saveStatus = MutableStateFlow<ContactSaveStatus>(ContactSaveStatus.Idle)
    val saveStatus: StateFlow<ContactSaveStatus> = _saveStatus.asStateFlow()

    init {
        val database = EmergencyInfoDatabase.getDatabase(application)
        repository = EmergencyRepository(
            profileDao = database.emergencyProfileDao(),
            contactDao = database.emergencyContactDao(),
            qrSettingsDao = database.qrCodeSettingsDao(),
            messageDao = database.emergencyMessageDao()
        )

        contacts = repository.getAllContacts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun showAddDialog() {
        clearForm()
        _editingContact.value = null
        _showDialog.value = true
    }

    fun showEditDialog(contact: EmergencyContact) {
        _editingContact.value = contact
        _contactName.value = contact.name
        _relationship.value = contact.relationship
        _phoneNumber.value = contact.phoneNumber
        _email.value = contact.email
        _isPrimary.value = contact.isPrimary
        _includeInQR.value = contact.includeInQR
        _showDialog.value = true
    }

    fun hideDialog() {
        _showDialog.value = false
        _validationErrors.value = emptyMap()
        _saveStatus.value = ContactSaveStatus.Idle
    }

    fun updateName(value: String) {
        _contactName.value = value
        clearFieldError("name")
    }

    fun updateRelationship(value: String) {
        _relationship.value = value
        clearFieldError("relationship")
    }

    fun updatePhoneNumber(value: String) {
        _phoneNumber.value = value
        clearFieldError("phone")
    }

    fun updateEmail(value: String) {
        _email.value = value
    }

    fun updateIsPrimary(value: Boolean) {
        _isPrimary.value = value
    }

    fun updateIncludeInQR(value: Boolean) {
        _includeInQR.value = value
    }

    private fun clearFieldError(field: String) {
        _validationErrors.value = _validationErrors.value.toMutableMap().apply {
            remove(field)
        }
    }

    fun saveContact() {
        val errors = validateContact()
        if (errors.isNotEmpty()) {
            _validationErrors.value = errors
            return
        }

        viewModelScope.launch {
            try {
                _saveStatus.value = ContactSaveStatus.Saving

                val contact = EmergencyContact(
                    id = _editingContact.value?.id ?: 0,
                    name = _contactName.value.trim(),
                    relationship = _relationship.value.trim(),
                    phoneNumber = _phoneNumber.value.trim(),
                    email = _email.value.trim(),
                    isPrimary = _isPrimary.value,
                    includeInQR = _includeInQR.value,
                    displayOrder = _editingContact.value?.displayOrder ?: 0
                )

                if (_editingContact.value != null) {
                    repository.updateContact(contact)
                } else {
                    repository.insertContact(contact)
                }

                // Update widget
                try {
                    com.eminfo.app.widget.EmergencyWidget.updateWidget(
                        getApplication<Application>().applicationContext
                    )
                } catch (e: Exception) {
                    // Widget update failed, but contact saved successfully
                }

                _saveStatus.value = ContactSaveStatus.Success
                kotlinx.coroutines.delay(500)
                hideDialog()
            } catch (e: Exception) {
                _saveStatus.value = ContactSaveStatus.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            try {
                repository.deleteContact(contact)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun validateContact(): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        if (_contactName.value.isBlank()) {
            errors["name"] = "Name is required"
        }

        if (_relationship.value.isBlank()) {
            errors["relationship"] = "Relationship is required"
        }

        if (_phoneNumber.value.isBlank()) {
            errors["phone"] = "Phone number is required"
        } else if (!isValidPhoneNumber(_phoneNumber.value)) {
            errors["phone"] = "Invalid phone format"
        }

        return errors
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val cleaned = phone.replace(Regex("[\\s()-]"), "")
        return cleaned.matches(Regex("^\\d{10,15}$"))
    }

    private fun clearForm() {
        _contactName.value = ""
        _relationship.value = ""
        _phoneNumber.value = ""
        _email.value = ""
        _isPrimary.value = false
        _includeInQR.value = true
        _validationErrors.value = emptyMap()
    }
}

sealed class ContactSaveStatus {
    object Idle : ContactSaveStatus()
    object Saving : ContactSaveStatus()
    object Success : ContactSaveStatus()
    data class Error(val message: String) : ContactSaveStatus()
}