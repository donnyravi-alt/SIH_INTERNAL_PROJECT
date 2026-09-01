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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AlertItem
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    viewModel: FeedCheckViewModel,
    language: AppLanguage,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU
    val alerts by viewModel.adminAlerts.collectAsState()
    var selectedVillageDialog by remember { mutableStateOf<String?>(null) }
    var trendViewMode by remember { mutableStateOf("Weekly") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PrimaryDarkGreen
                    )
                }
                Column {
                    Text(
                        text = if (isTelugu) "సహకార నిర్వాహక డ్యాష్‌బోర్డ్" else "FeedCheck Overview",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )
                    Text(
                        text = if (isTelugu) "కృష్ణా మిల్క్ యూనియన్ • లైవ్ క్వాలిటీ మానిటరింగ్" else "Krishna Milk Union • Live Quality Monitoring",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariantDark
                        )
                    )
                }
            }
        }

        // Metrics Row (3 cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = if (isTelugu) "మొత్తం రైతులు" else "Total Farmers",
                value = "1,240",
                change = "↑ 2.4%",
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = if (isTelugu) "పరీక్షించిన నమూనాలు" else "Samples Tested",
                value = "5,800",
                change = "↑ 5.1%",
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = if (isTelugu) "హై-రిస్క్ హెచ్చరికలు" else "High-Risk Alerts",
                value = "12",
                change = "Action Req.",
                isPositive = false,
                modifier = Modifier.weight(1f)
            )
        }

        // Regional Risk Map Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "ప్రాంతీయ ప్రమాద పటం (Regional Risk Map)" else "Regional Risk Map",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )

                    // Map legend
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        LegendDot(color = SecondaryGreen, label = "Safe")
                        LegendDot(color = WarningOrange, label = "Warning")
                        LegendDot(color = ErrorRed, label = "High")
                    }
                }

                // Interactive stylized simulated district map container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerMid)
                        .border(1.dp, BrandCardBorder, RoundedCornerShape(12.dp))
                ) {
                    // Stylized grid background
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val step = 40.dp.toPx()
                        for (x in 0..(size.width / step).toInt()) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.5f),
                                start = androidx.compose.ui.geometry.Offset(x * step, 0f),
                                end = androidx.compose.ui.geometry.Offset(x * step, size.height),
                                strokeWidth = 1f
                            )
                        }
                    }

                    // Region Pin 1: Palem (High Risk)
                    MapPin(
                        name = "Village Palem",
                        statusColor = ErrorRed,
                        xOffset = 50.dp,
                        yOffset = 40.dp,
                        onClick = { selectedVillageDialog = "Village Palem: 5 High Aflatoxin cases detected in dry fodder." }
                    )

                    // Region Pin 2: Guntur Center (Safe)
                    MapPin(
                        name = "Guntur East",
                        statusColor = SecondaryGreen,
                        xOffset = 180.dp,
                        yOffset = 30.dp,
                        onClick = { selectedVillageDialog = "Guntur East: 98% Optimal quality. 420 samples tested." }
                    )

                    // Region Pin 3: Kodur (Warning)
                    MapPin(
                        name = "Kodur",
                        statusColor = WarningOrange,
                        xOffset = 90.dp,
                        yOffset = 120.dp,
                        onClick = { selectedVillageDialog = "Kodur: High moisture in 8 silage samples. Aeration advised." }
                    )

                    // Region Pin 4: Nuzendla (Safe)
                    MapPin(
                        name = "Nuzendla",
                        statusColor = SecondaryGreen,
                        xOffset = 230.dp,
                        yOffset = 130.dp,
                        onClick = { selectedVillageDialog = "Nuzendla: 100% Quality pass rate. Zero mould found." }
                    )
                }

                if (selectedVillageDialog != null) {
                    Surface(
                        color = SurfaceContainerHigh,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedVillageDialog ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = OnSurfaceDark
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { selectedVillageDialog = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceDark)
                            }
                        }
                    }
                }
            }
        }

        // Recent Alerts List
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isTelugu) "ఇటీవలి హెచ్చరికలు (Recent Alerts)" else "Recent Alerts",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                )

                alerts.forEach { alert ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (alert.severity == "High") ErrorRed else WarningOrange)
                            )

                            Column {
                                Text(
                                    text = "${alert.farmerName} (${alert.village})",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurfaceDark
                                    )
                                )
                                Text(
                                    text = "Alert: ${alert.alertType} • ${alert.timeAgo}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = OnSurfaceVariantDark
                                    )
                                )
                            }
                        }

                        StatusBadge(status = alert.severity)
                    }
                    Divider(color = SurfaceContainerMid)
                }
            }
        }

        // 30-Day Quality Trend Compliance Chart
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "నాణ్యత ట్రెండ్ (30-Day Compliance)" else "Quality Trend (30 Days)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )

                    Row {
                        FilterChip(
                            selected = trendViewMode == "Weekly",
                            onClick = { trendViewMode = "Weekly" },
                            label = { Text("Weekly") }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = trendViewMode == "Monthly",
                            onClick = { trendViewMode = "Monthly" },
                            label = { Text("Monthly") }
                        )
                    }
                }

                // Custom bar graph with Compose
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val days = listOf("W1", "W2", "W3", "W4", "W5", "W6", "W7")
                    val heights = listOf(0.72f, 0.85f, 0.65f, 0.90f, 0.94f, 0.88f, 0.96f)

                    days.zip(heights).forEach { (day, fraction) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .fillMaxHeight(fraction)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(if (fraction > 0.8f) PrimaryDarkGreen else WarningOrange)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariantDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    change: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.border(1.dp, BrandCardBorder, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariantDark,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
            Text(
                text = change,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) SecondaryGreen else ErrorRed
                )
            )
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = OnSurfaceVariantDark
            )
        )
    }
}

@Composable
private fun MapPin(
    name: String,
    statusColor: Color,
    xOffset: androidx.compose.ui.unit.Dp,
    yOffset: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset)
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .border(2.dp, Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = OnSurfaceDark
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
