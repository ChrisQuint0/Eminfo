package com.eminfo.app.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import com.eminfo.app.data.local.entities.EmergencyMessage
import com.eminfo.app.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EmergencyMessageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmergencyRepository

    private val _messageTemplate = MutableStateFlow("Emergency. I need help at [location].")
    val messageTemplate: StateFlow<String> = _messageTemplate.asStateFlow()

    private val _includeLocation = MutableStateFlow(true)
    val includeLocation: StateFlow<Boolean> = _includeLocation.asStateFlow()

    private val _saveStatus = MutableStateFlow<SaveStatus>(SaveStatus.Idle)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    init {
        val database = EmergencyInfoDatabase.getDatabase(application)
        repository = EmergencyRepository(
            profileDao = database.emergencyProfileDao(),
            contactDao = database.emergencyContactDao(),
            qrSettingsDao = database.qrCodeSettingsDao(),
            messageDao = database.emergencyMessageDao()
        )

        loadMessage()
    }

    private fun loadMessage() {
        viewModelScope.launch {
            repository.getEmergencyMessage().collect { message ->
                message?.let {
                    _messageTemplate.value = it.messageTemplate
                    _includeLocation.value = it.includeLocation
                }
            }
        }
    }

    fun updateTemplate(template: String) {
        _messageTemplate.value = template
    }

    fun updateIncludeLocation(include: Boolean) {
        _includeLocation.value = include
    }

    fun saveMessage() {
        viewModelScope.launch {
            try {
                _saveStatus.value = SaveStatus.Saving

                val message = EmergencyMessage(
                    messageTemplate = _messageTemplate.value,
                    includeLocation = _includeLocation.value
                )

                repository.saveEmergencyMessage(message)
                _saveStatus.value = SaveStatus.Success

                kotlinx.coroutines.delay(2000)
                _saveStatus.value = SaveStatus.Idle
            } catch (e: Exception) {
                _saveStatus.value = SaveStatus.Error(e.message ?: "Failed to save")
            }
        }
    }

    sealed class SaveStatus {
        object Idle : SaveStatus()
        object Saving : SaveStatus()
        object Success : SaveStatus()
        data class Error(val message: String) : SaveStatus()
    }
}