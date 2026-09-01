package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AppLanguage
import com.example.ui.FeedScanStep
import com.example.ui.NavigationScreen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    language: AppLanguage,
    onNavigate: (NavigationScreen) -> Unit,
    onStartScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome & Logo Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCqbpaiUXPe-lJoi5VW-499yINpfTwdj1dBNnHHqvGUfxADCIYNXaPNdMZx7VzrM2FY4NShi-irVOMHMVd1qeRYtLkW-O9e_w_p78JuyT3VFJnPXf1wjeYuveSVJs17iRHfwJKB81ArvwplIISk-GWbrbgQePee7m3dXpI1ZAMbUF-f6PsOaW-kF2owCLmBpHGKgrDUS3A13Be07jlra5vngC0QASEf_UyhaGTgF2duCyJiwA2XQ2Pe",
                    contentDescription = "FeedCheck Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isTelugu) "తిరిగి స్వాగతం, రైతు మిత్రమా" else "Welcome back,\nFarmer",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isTelugu) "నేటి నాణ్యత తనిఖీలకు సిద్ధంగా ఉన్నారా?" else "Ready for today's quality checks?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = OnSurfaceVariantDark
                ),
                textAlign = TextAlign.Center
            )
        }

        // Recent Summary Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(12.dp))
                .testTag("recent_summary_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = OnSecondaryContainerGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isTelugu) "చివరి తనిఖీ: 2 గంటల క్రితం" else "Last Check: 2 hours ago",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        Text(
                            text = if (isTelugu) "స్థితి: అధిక నాణ్యత (High Quality)" else "Status: High Quality",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariantDark
                            )
                        )
                    }
                }

                Text(
                    text = if (isTelugu) "వివరాలు" else "View Details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryDarkGreen
                    ),
                    modifier = Modifier
                        .clickable { onNavigate(NavigationScreen.HISTORY) }
                        .padding(4.dp)
                )
            }
        }

        // Action 1: Primary Check Cattle Feed Card
        Surface(
            color = PrimaryDarkGreen,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStartScan() }
                .testTag("check_cattle_feed_button")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Biotech,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column {
                    Text(
                        text = if (isTelugu) "పశుగ్రాసం తనిఖీ చేయండి" else "Check Cattle Feed",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isTelugu) "కొత్త నాణ్యత పరీక్ష ప్రారంభించండి" else "Start new quality test",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    )
                }
            }
        }

        // Action 2: Water Settling Test Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
                .clickable { onNavigate(NavigationScreen.WATER_TEST) }
                .testTag("water_settling_test_button")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = PrimaryDarkGreen,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Column {
                    Text(
                        text = if (isTelugu) "నీటి సెట్లింగ్ పరీక్ష" else "Water Settling Test",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )
                    Text(
                        text = if (isTelugu) "నీటి స్వచ్ఛతను ధృవీకరించండి" else "Verify water purity",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceVariantDark
                        )
                    )
                }
            }
        }

        // Action 3: My Test History
        ActionRowCard(
            icon = Icons.Default.History,
            title = if (isTelugu) "నా పరీక్షల చరిత్ర" else "My Test History",
            subtitle = if (isTelugu) "గత ఫలితాలను వీక్షించండి" else "View past results",
            onClick = { onNavigate(NavigationScreen.HISTORY) },
            testTag = "my_test_history_button"
        )

        // Action 4: Farm Profile
        ActionRowCard(
            icon = Icons.Default.Storefront,
            title = if (isTelugu) "వ్యవసాయ ప్రొఫైల్" else "Farm Profile",
            subtitle = if (isTelugu) "పశువులు & వివరాల నిర్వహణ" else "Manage cattle & details",
            onClick = { onNavigate(NavigationScreen.PROFILE) },
            testTag = "farm_profile_button"
        )

        // Action 5: AI Cattle Nutrition Advisor
        Surface(
            color = PrimaryFixedGreen,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .clickable { onNavigate(NavigationScreen.CHATBOT) }
                .testTag("ai_cattle_advisor_button")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryDarkGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTelugu) "AI పశు పోషకాహార సలహాదారు" else "AI Cattle Nutrition Advisor",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnPrimaryFixedGreen
                        )
                    )
                    Text(
                        text = if (isTelugu) "Gemini తో వాయిస్ & చాట్ సంభాషణ" else "Talk or chat with Gemini AI",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnPrimaryFixedGreen.copy(alpha = 0.85f)
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = OnPrimaryFixedGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ActionRowCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainerMid)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryDarkGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariantDark
                    )
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = OnSurfaceVariantDark
            )
        }
    }
}
