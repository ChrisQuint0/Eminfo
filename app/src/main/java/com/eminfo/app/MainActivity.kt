package com.eminfo.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.eminfo.app.presentation.screens.*
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var hasSeenOnboarding by remember {
        mutableStateOf(prefs.getBoolean("has_seen_onboarding", false))
    }
    var currentScreen by remember { mutableStateOf("profile") }

    if (!hasSeenOnboarding) {
        OnboardingScreen(
            onComplete = {
                prefs.edit().putBoolean("has_seen_onboarding", true).apply()
                hasSeenOnboarding = true
            }
        )
    } else {
        when (currentScreen) {
            "profile" -> ProfileScreen(
                onNavigateToContacts = { currentScreen = "contacts" },
                onNavigateToQR = { currentScreen = "qr" },
                onNavigateToWidget = { currentScreen = "widget" },
                onNavigateToSettings = { currentScreen = "settings" }
            )
            "contacts" -> ContactsScreen(
                onNavigateBack = { currentScreen = "profile" }
            )
            "qr" -> QRCodeScreen(
                onNavigateBack = { currentScreen = "profile" }
            )
            "widget" -> WidgetSetupScreen(
                onNavigateBack = { currentScreen = "profile" }
            )
            "settings" -> SettingsScreen(
                onNavigateBack = { currentScreen = "profile" }
            )
        }
    }
}