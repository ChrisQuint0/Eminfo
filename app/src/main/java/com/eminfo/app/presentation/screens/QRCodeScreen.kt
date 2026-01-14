package com.eminfo.app.presentation.screens

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eminfo.app.presentation.viewmodel.QRCodeViewModel
import com.eminfo.app.presentation.viewmodel.QRField
import com.eminfo.app.util.QRCodeGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun QRCodeScreen(
    viewModel: QRCodeViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val qrBitmap by viewModel.qrCodeBitmap.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val preview by viewModel.qrDataPreview.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            QRCodeTopBar(
                onNavigateBack = onNavigateBack,
                onShare = {
                    qrBitmap?.let { bitmap ->
                        shareQRCode(context, bitmap)
                    }
                },
                onDownload = {
                    qrBitmap?.let { bitmap ->
                        downloadQRCard(context, bitmap)
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // QR Code Display Card
            QRCodeDisplayCard(qrBitmap, isGenerating)

            // Data Preview Card
            DataPreviewCard(preview)

            // Settings Card
            QRSettingsCard(
                settings = settings,
                onSettingChange = { field, enabled ->
                    viewModel.updateSetting(field, enabled)
                }
            )

            // Info Card
            InfoBanner()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeTopBar(
    onNavigateBack: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
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
                    "Emergency QR Code",
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
            actions = {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun QRCodeDisplayCard(bitmap: Bitmap?, isGenerating: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFF00b761).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = Color(0xFF00b761),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    "Your Emergency QR Code",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E)
                )
            }

            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            color = Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Sync,
                            contentDescription = null,
                            tint = Color(0xFF00b761),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Generating QR Code...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
            } else if (bitmap != null) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Emergency QR Code",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(
                            color = Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "No data selected",
                            color = Color(0xFF8E8E93)
                        )
                    }
                }
            }

            Text(
                "First responders can scan this code to access your emergency information",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF8E8E93),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun DataPreviewCard(preview: String) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Preview,
                    contentDescription = null,
                    tint = Color(0xFF007AFF)
                )
                Text(
                    "QR Code Contains",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (preview.isNotBlank()) {
                Text(
                    preview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1C1C1E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF8F9FA),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                )
            } else {
                Text(
                    "Select data fields below to include in QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8E8E93),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun QRSettingsCard(
    settings: com.eminfo.app.data.local.entities.QRCodeSettings,
    onSettingChange: (QRField, Boolean) -> Unit
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF00b761)
                )
                Text(
                    "Customize QR Data",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            QRSettingItem(
                label = "Full Name",
                checked = settings.includeName,
                onCheckedChange = { onSettingChange(QRField.NAME, it) }
            )

            QRSettingItem(
                label = "Blood Type",
                checked = settings.includeBloodType,
                onCheckedChange = { onSettingChange(QRField.BLOOD_TYPE, it) }
            )

            QRSettingItem(
                label = "Allergies",
                checked = settings.includeAllergies,
                onCheckedChange = { onSettingChange(QRField.ALLERGIES, it) }
            )

            QRSettingItem(
                label = "Medical Conditions",
                checked = settings.includeMedicalConditions,
                onCheckedChange = { onSettingChange(QRField.MEDICAL_CONDITIONS, it) }
            )

            QRSettingItem(
                label = "Current Medications",
                checked = settings.includeMedications,
                onCheckedChange = { onSettingChange(QRField.MEDICATIONS, it) }
            )

            QRSettingItem(
                label = "Primary Emergency Contact",
                checked = settings.includePrimaryContact,
                onCheckedChange = { onSettingChange(QRField.PRIMARY_CONTACT, it) }
            )

            QRSettingItem(
                label = "Physician Information",
                checked = settings.includePhysician,
                onCheckedChange = { onSettingChange(QRField.PHYSICIAN, it) }
            )
        }
    }
}

@Composable
fun QRSettingItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (checked) Color(0xFF00b761).copy(alpha = 0.05f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF1C1C1E)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00b761)
            )
        )
    }
}

@Composable
fun InfoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF007AFF).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF007AFF)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Privacy & Security",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF007AFF)
                )
                Text(
                    "Your QR code is generated locally on your device. No data is sent to external servers. Only share with trusted individuals.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1C1C1E)
                )
            }
        }
    }
}

private fun shareQRCode(context: android.content.Context, bitmap: Bitmap) {
    try {
        val file = File(context.cacheDir, "emergency_qr.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "My Emergency Information QR Code")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun downloadQRCard(context: android.content.Context, qrBitmap: Bitmap) {
    try {
        // Generate the downloadable card
        val cardBitmap = QRCodeGenerator.generateDownloadableQRCard(context, qrBitmap)

        if (cardBitmap == null) {
            Toast.makeText(context, "Failed to generate QR card", Toast.LENGTH_SHORT).show()
            return
        }

        // Save to gallery
        val filename = "EmInfo_QR_${System.currentTimeMillis()}.png"
        val fos: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 and above
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/EmInfo")
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            // Android 9 and below
            val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_PICTURES
            ).toString() + "/EmInfo"

            val dir = File(imagesDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            cardBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(context, "QR Code saved to gallery", Toast.LENGTH_SHORT).show()
        }

        cardBitmap.recycle()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to save QR code: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}