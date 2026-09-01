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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: FeedCheckViewModel,
    language: AppLanguage,
    onNavigate: (NavigationScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Farmer Info Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerGreen)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = OnSecondaryContainerGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isTelugu) "రమేష్ కుమార్" else "Ramesh Kumar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurfaceDark
                            )
                        )
                        Text(
                            text = "Farmer ID: #FC-8942",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = OnSurfaceVariantDark
                            )
                        )
                    }

                    Surface(
                        color = OptimalGreen,
                        shape = CircleShape
                    ) {
                        Text(
                            text = if (isTelugu) "యాక్టివ్" else "Active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnOptimalGreen
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Divider(color = BrandCardBorder)

                // Detail Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailTile(
                        label = if (isTelugu) "గ్రామం / జిల్లా" else "Village / District",
                        value = "Guntur District, AP",
                        modifier = Modifier.weight(1f)
                    )
                    DetailTile(
                        label = if (isTelugu) "పాడి సహకార సంఘం" else "Dairy Cooperative",
                        value = "Krishna Milk Union",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailTile(
                        label = if (isTelugu) "పశువుల సంఖ్య" else "Total Cattle",
                        value = "14 Cows & Buffaloes",
                        modifier = Modifier.weight(1f)
                    )
                    DetailTile(
                        label = if (isTelugu) "సగటు రోజువారీ పాలు" else "Daily Milk Yield",
                        value = "180 Liters / Day",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Settings & Actions
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                ProfileOptionRow(
                    icon = Icons.Default.Translate,
                    title = if (isTelugu) "భాష మార్పు (Language: తెలుగు)" else "Language (Current: English)",
                    subtitle = if (isTelugu) "ఆంగ్లం / తెలుగు" else "English / Telugu",
                    onClick = { viewModel.toggleLanguage() }
                )

                Divider(color = BrandCardBorder)

                ProfileOptionRow(
                    icon = Icons.Default.SmartToy,
                    title = if (isTelugu) "AI పశు సలహాదారు (Gemini Chat)" else "AI Cattle Advisor (Gemini Chat)",
                    subtitle = if (isTelugu) "పోషకాహారం & రోగ నిరోధక సలహాలు" else "Nutrition & preventive care assistant",
                    onClick = { onNavigate(NavigationScreen.CHATBOT) }
                )

                Divider(color = BrandCardBorder)

                ProfileOptionRow(
                    icon = Icons.Default.AdminPanelSettings,
                    title = if (isTelugu) "సహకార నిర్వాహక డ్యాష్‌బోర్డ్" else "Cooperative Admin Dashboard",
                    subtitle = if (isTelugu) "ప్రాంతీయ నాణ్యతా రికార్డులు" else "Regional risk map & bulk testing metrics",
                    onClick = { onNavigate(NavigationScreen.ADMIN_DASHBOARD) }
                )

                Divider(color = BrandCardBorder)

                ProfileOptionRow(
                    icon = Icons.Default.Headphones,
                    title = if (isTelugu) "సహాయం & మద్దతు" else "Help & Kisan Helpline",
                    subtitle = "1800-180-1551 (Toll-Free)",
                    onClick = { /* Helpline dial action */ }
                )
            }
        }

        Text(
            text = "FeedCheck QC v2.1.4 • AgriQC Smart Suite",
            style = MaterialTheme.typography.labelSmall.copy(color = OutlineColor)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DetailTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = SurfaceContainerMid,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariantDark,
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SurfaceContainerMid)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryDarkGreen,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
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
