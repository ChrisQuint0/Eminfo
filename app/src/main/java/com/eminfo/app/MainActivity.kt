package com.eminfo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.eminfo.app.presentation.screens.ContactsScreen
import com.eminfo.app.presentation.screens.ProfileScreen
import com.eminfo.app.presentation.screens.QRCodeScreen
import com.eminfo.app.ui.theme.EmergencyInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmergencyInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EmergencyInfoApp()
                }
            }
        }
    }
}

@Composable
fun EmergencyInfoApp() {
    var currentScreen by remember { mutableStateOf("profile") }

    when (currentScreen) {
        "profile" -> ProfileScreen(
            onNavigateToContacts = { currentScreen = "contacts" },
            onNavigateToQR = { currentScreen = "qr" }
        )
        "contacts" -> ContactsScreen(
            onNavigateBack = { currentScreen = "profile" }
        )
        "qr" -> QRCodeScreen(
            onNavigateBack = { currentScreen = "profile" }
        )
    }
}