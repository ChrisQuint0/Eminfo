package com.eminfo.app.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eminfo.app.presentation.viewmodel.ProfileField
import com.eminfo.app.presentation.viewmodel.ProfileViewModel
import com.eminfo.app.presentation.viewmodel.SaveStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToContacts: () -> Unit = {}
) {
    val profile by viewModel.profileState.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()
    val validationErrors by viewModel.validationErrors.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveStatus) {
        when (saveStatus) {
            is SaveStatus.Success -> {
                snackbarHostState.showSnackbar(
                    message = "✓ Profile saved successfully!",
                    duration = SnackbarDuration.Short
                )
            }
            is SaveStatus.Error -> {
                snackbarHostState.showSnackbar(
                    message = "✗ ${(saveStatus as SaveStatus.Error).message}",
                    duration = SnackbarDuration.Long
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = { ModernTopBar(onNavigateToContacts) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Hero Header
                HeroHeader()

                // Personal Information Card
                InfoCard(
                    title = "Personal Information",
                    icon = Icons.Outlined.Person,
                    iconTint = Color(0xFF007AFF)
                ) {
                    ModernTextField(
                        value = profile?.fullName ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.FULL_NAME, it) },
                        label = "Full Name",
                        icon = Icons.Filled.Person,
                        isRequired = true,
                        errorMessage = validationErrors[ProfileField.FULL_NAME.name]
                    )

                    ModernTextField(
                        value = profile?.dateOfBirth ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.DATE_OF_BIRTH, it) },
                        label = "Date of Birth",
                        icon = Icons.Filled.Cake,
                        placeholder = "MM/DD/YYYY"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ModernTextField(
                            value = profile?.bloodType ?: "",
                            onValueChange = { viewModel.updateField(ProfileField.BLOOD_TYPE, it) },
                            label = "Blood Type",
                            icon = Icons.Filled.Bloodtype,
                            placeholder = "O+",
                            modifier = Modifier.weight(1f)
                        )

                        ModernTextField(
                            value = profile?.height ?: "",
                            onValueChange = { viewModel.updateField(ProfileField.HEIGHT, it) },
                            label = "Height",
                            icon = Icons.Filled.Height,
                            placeholder = "5'10\"",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Medical Information Card
                InfoCard(
                    title = "Medical Information",
                    icon = Icons.Outlined.LocalHospital,
                    iconTint = Color(0xFFDC3545)
                ) {
                    ModernTextField(
                        value = profile?.allergies ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.ALLERGIES, it) },
                        label = "Allergies",
                        icon = Icons.Filled.Warning,
                        placeholder = "Penicillin, Peanuts...",
                        singleLine = false,
                        minLines = 2
                    )

                    ModernTextField(
                        value = profile?.medicalConditions ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.MEDICAL_CONDITIONS, it) },
                        label = "Medical Conditions",
                        icon = Icons.Filled.LocalHospital,
                        placeholder = "Diabetes, Asthma...",
                        singleLine = false,
                        minLines = 2
                    )

                    ModernTextField(
                        value = profile?.currentMedications ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.MEDICATIONS, it) },
                        label = "Current Medications",
                        icon = Icons.Filled.Medication,
                        placeholder = "Aspirin 100mg daily...",
                        singleLine = false,
                        minLines = 2
                    )
                }

                // Physician Information Card
                InfoCard(
                    title = "Physician Information",
                    icon = Icons.Outlined.MedicalServices,
                    iconTint = Color(0xFF00b761)
                ) {
                    ModernTextField(
                        value = profile?.physicianName ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.PHYSICIAN_NAME, it) },
                        label = "Physician Name",
                        icon = Icons.Filled.MedicalServices,
                        placeholder = "Dr. John Smith"
                    )

                    ModernTextField(
                        value = profile?.physicianPhone ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.PHYSICIAN_PHONE, it) },
                        label = "Physician Phone",
                        icon = Icons.Filled.Phone,
                        placeholder = "(555) 123-4567",
                        errorMessage = validationErrors[ProfileField.PHYSICIAN_PHONE.name]
                    )
                }

                // Insurance Information Card
                InfoCard(
                    title = "Insurance Information",
                    icon = Icons.Outlined.HealthAndSafety,
                    iconTint = Color(0xFFFF9500)
                ) {
                    ModernTextField(
                        value = profile?.insuranceProvider ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.INSURANCE_PROVIDER, it) },
                        label = "Insurance Provider",
                        icon = Icons.Filled.HealthAndSafety,
                        placeholder = "Blue Cross Blue Shield"
                    )

                    ModernTextField(
                        value = profile?.insurancePolicyNumber ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.INSURANCE_POLICY, it) },
                        label = "Policy Number",
                        icon = Icons.Filled.CardMembership,
                        placeholder = "ABC123456789"
                    )
                }

                // Additional Notes Card
                InfoCard(
                    title = "Additional Notes",
                    icon = Icons.Outlined.Notes,
                    iconTint = Color(0xFF8E8E93)
                ) {
                    ModernTextField(
                        value = profile?.additionalNotes ?: "",
                        onValueChange = { viewModel.updateField(ProfileField.ADDITIONAL_NOTES, it) },
                        label = "Notes",
                        icon = Icons.Filled.Notes,
                        placeholder = "Any other important information...",
                        singleLine = false,
                        minLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Floating Save Button
            ModernFloatingButton(
                saveStatus = saveStatus,
                onClick = { viewModel.saveProfile() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTopBar(onNavigateToContacts: () -> Unit) {
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
                    "Emergency Profile",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ),
            actions = {
                IconButton(
                    onClick = onNavigateToContacts,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = "Contacts",
                        tint = Color.White
                    )
                }
            }
        )
    }
}

@Composable
fun HeroHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00b761).copy(alpha = 0.1f),
                            Color(0xFF00d670).copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFF00b761),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.HealthAndSafety,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    "Life-Saving Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1E)
                )
                Text(
                    "Keep your emergency details up to date",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8E8E93)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
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
                            color = iconTint.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1C1C1E)
                )
            }

            content()
        }
    }
}

@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String = "",
    isRequired: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label + if (isRequired) " *" else "") },
        placeholder = { Text(placeholder, color = Color(0xFFAAAAAA)) },
        leadingIcon = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (errorMessage != null) Color(0xFFDC3545) else Color(0xFF8E8E93)
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        isError = errorMessage != null,
        supportingText = errorMessage?.let { { Text(it, color = Color(0xFFDC3545)) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00b761),
            focusedLabelColor = Color(0xFF00b761),
            unfocusedBorderColor = Color(0xFFE5E5EA),
            focusedContainerColor = Color(0xFF00b761).copy(alpha = 0.03f),
            unfocusedContainerColor = Color(0xFFF8F9FA)
        )
    )
}

@Composable
fun ModernFloatingButton(
    saveStatus: SaveStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSaving = saveStatus is SaveStatus.Saving

    Button(
        onClick = onClick,
        enabled = !isSaving,
        modifier = modifier
            .height(56.dp)
            .widthIn(min = 200.dp)
            .shadow(8.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00b761),
            disabledContainerColor = Color(0xFF00b761).copy(alpha = 0.6f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 12.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSaving) {
                // Custom loading dots animation - no CircularProgressIndicator
                LoadingDots()

                Spacer(Modifier.width(12.dp))
                Text(
                    "Saving...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            } else {
                Icon(
                    Icons.Filled.Save,
                    contentDescription = "Save",
                    tint = Color.White
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Save Profile",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = LinearEasing, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )

            Box(
                modifier = Modifier
                    .size((6.dp.value * scale).dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}