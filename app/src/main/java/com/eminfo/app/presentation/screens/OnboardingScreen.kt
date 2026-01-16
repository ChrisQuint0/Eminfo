package com.eminfo.app.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import com.eminfo.app.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val pages = listOf(
        OnboardingPage(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.eminfo_logo),
                    contentDescription = "App Logo",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Welcome to Eminfo",
            description = "A life-saving app that provides quick access to your critical medical information in emergency situations.",
            color = Color(0xFF00b761)
        ),
        OnboardingPage(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Create Your Profile",
            description = "Add your medical conditions, allergies, medications, blood type, and physician information.",
            color = Color(0xFF007AFF)
        ),
        OnboardingPage(
            icon = {
                Icon(
                    Icons.Default.ContactPhone,
                    contentDescription = null,
                    tint = Color(0xFFFF9500),
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Emergency Contacts",
            description = "Add contacts who should be notified in an emergency. Set a primary contact for instant access.",
            color = Color(0xFFFF9500)
        ),
        OnboardingPage(
            icon = {
                Icon(
                    Icons.Default.QrCode2,
                    contentDescription = null,
                    tint = Color(0xFFDC3545),
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "QR Code Access",
            description = "Generate a QR code with your emergency info. First responders can scan it for instant access.",
            color = Color(0xFFDC3545)
        ),
        OnboardingPage(
            icon = {
                Icon(
                    Icons.Default.Widgets,
                    contentDescription = null,
                    tint = Color(0xFF00b761),
                    modifier = Modifier.size(64.dp)
                )
            },
            title = "Quick Access Widget",
            description = "Add a home screen widget for instant access to your emergency information anytime.",
            color = Color(0xFF00b761)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Pager
            HorizontalPager(
                count = pages.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Bottom Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Page Indicator
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    activeColor = Color(0xFF00b761),
                    inactiveColor = Color(0xFFE5E5EA),
                    indicatorWidth = 8.dp,
                    indicatorHeight = 8.dp,
                    spacing = 8.dp
                )

                // Buttons
                if (pagerState.currentPage == pages.size - 1) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(28.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00b761)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            "Get Started",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onComplete,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Skip")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(28.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00b761)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Next")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            page.color.copy(alpha = 0.2f),
                            page.color.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            page.icon() // Call the composable lambda
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF8E8E93),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )
    }
}

data class OnboardingPage(
    val icon: @Composable () -> Unit, // Changed to composable lambda
    val title: String,
    val description: String,
    val color: Color
)