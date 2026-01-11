package com.eminfo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eminfo.app.data.local.database.EmergencyInfoDatabase
import com.eminfo.app.ui.theme.EmergencyInfoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = EmergencyInfoDatabase.getDatabase(applicationContext)

        setContent {
            EmergencyInfoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DatabaseTestScreen(database)
                }
            }
        }
    }
}

@Composable
fun DatabaseTestScreen(database: EmergencyInfoDatabase) {
    var testResult by remember { mutableStateOf("Testing database...") }

    LaunchedEffect(Unit) {
        testResult = withContext(Dispatchers.IO) {
            try {
                // Test database access
                val profile = database.emergencyProfileDao().getProfileOnce()
                val contacts = database.emergencyContactDao().getAllContacts()

                "✅ Database initialized successfully!\n" +
                        "Profile: ${if (profile == null) "Not set" else "Exists"}\n" +
                        "Ready for Day 2 development!"
            } catch (e: Exception) {
                "❌ Error: ${e.message}"
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = testResult,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}