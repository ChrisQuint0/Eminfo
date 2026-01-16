package com.eminfo.app.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eminfo.app.R
import com.eminfo.app.presentation.viewmodel.EmergencyMessageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: EmergencyMessageViewModel = viewModel()
    val messageTemplate by viewModel.messageTemplate.collectAsState()
    val includeLocation by viewModel.includeLocation.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }


    LaunchedEffect(saveStatus) {
        when (saveStatus) {
            is EmergencyMessageViewModel.SaveStatus.Success -> {
                snackbarHostState.showSnackbar("✓ Message saved!")
            }
            is EmergencyMessageViewModel.SaveStatus.Error -> {
                snackbarHostState.showSnackbar("Error saving message")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00b761),
                                Color(0xFF00d670)
                            )
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Settings",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Info Section
            SectionCard(title = "App Information") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Version 1.0",
                    onClick = { showAboutDialog = true }
                )

                Divider(color = Color(0xFFE5E5EA))

                SettingsItem(
                    icon = Icons.Default.Code,
                    title = "Open Source",
                    subtitle = "View on GitHub",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ChrisQuint0/Eminfo"))
                        context.startActivity(intent)
                    }
                )

                Divider(color = Color(0xFFE5E5EA))

                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy",
                    subtitle = "Read our Privacy Policy",
                    onClick = { showPrivacyDialog = true}
                )
            }

            // Support Section
            SectionCard(title = "Support") {
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help & Feedback",
                    subtitle = "Get help or send feedback",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:cquinto.primary@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Eminfo Feedback")
                        }
                        context.startActivity(intent)
                    }
                )

                Divider(color = Color(0xFFE5E5EA))

                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "Rate App",
                    subtitle = "Share your experience",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:cquinto.primary@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Eminfo Rating")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // Version Info
            Text(
                "Eminfo v1.0\nBuilt by Christopher Quinto with love for safety",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E8E93),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.eminfo_logo),
                    contentDescription = "App Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Eminfo",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Developed by Christopher Quinto • Version 1.0")
                    Text(
                        "A life-saving app that provides quick access to critical medical information in emergency situations.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "I, Christopher Quinto is committed to protecting your privacy and ensuring the security of your personal information. " +
                                "Eminfo is designed with privacy as a core principle—all of your data, including medical information, " +
                                "emergency contacts, blood type, medications, allergies, and any other personal details you choose to enter, " +
                                "is stored exclusively on your device. I do not collect, transmit, or share any of your information with " +
                                "external servers or third parties. There are no analytics, no tracking, and no data mining of any kind.\n\n" +
                                "This application requires phone permission solely to enable you to quickly call your designated emergency " +
                                "contacts when needed. This permission is only used when you explicitly choose to initiate a call through " +
                                "the app interface and widget. I understand that the information you entrust to Eminfo is sensitive and " +
                                "potentially life-saving. That's why I've built this app to function entirely offline, ensuring that your " +
                                "medical information, emergency contact information, and personal data never leaves your device. You maintain " +
                                "complete ownership and control of your information at all times. Should you choose to uninstall the app, " +
                                "all of your data will be permanently removed from your device with no copies retained anywhere else.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = Color(0xFF00b761))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFF00b761).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color(0xFF00b761),
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1C1C1E)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E8E93)
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFF8E8E93)
        )
    }
}