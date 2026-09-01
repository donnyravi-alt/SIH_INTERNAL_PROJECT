package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FeedTestEntity
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: FeedCheckViewModel,
    language: AppLanguage,
    onSelectTest: (FeedTestEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU
    val testList by viewModel.testHistory.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredList = remember(testList, selectedFilter) {
        when (selectedFilter) {
            "FEED" -> testList.filter { it.testType == "CATTLE_FEED" }
            "WATER" -> testList.filter { it.testType == "WATER_TEST" }
            else -> testList
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Nutrition Advisory Banner
        item {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "పోషణ సలహా (Advisory)" else "Nutrition Advisory",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkGreen
                            )
                        )

                        IconButton(
                            onClick = { viewModel.speakCurrentAdvisory() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SecondaryContainerGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen",
                                tint = OnSecondaryContainerGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isTelugu) "ప్రస్తుత ఫీడ్ మిశ్రమంలో ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము." else "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing the alfalfa ratio by 15%.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurfaceDark
                        )
                    )
                }
            }
        }

        // Filters row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text(if (isTelugu) "అన్నీ (${testList.size})" else "All (${testList.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "FEED",
                    onClick = { selectedFilter = "FEED" },
                    label = { Text(if (isTelugu) "దాణా పరీక్షలు" else "Cattle Feed") }
                )
                FilterChip(
                    selected = selectedFilter == "WATER",
                    onClick = { selectedFilter = "WATER" },
                    label = { Text(if (isTelugu) "నీటి పరీక్షలు" else "Water Tests") }
                )
            }
        }

        item {
            Text(
                text = if (isTelugu) "పరీక్షా రికార్డులు (Test Records)" else "Test Records",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            )
        }

        items(filteredList, key = { it.id }) { test ->
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(14.dp))
                    .clickable { onSelectTest(test) }
                    .testTag("history_item_${test.id}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (test.testType == "WATER_TEST") SecondaryContainerGreen else SurfaceContainerMid)
                        ) {
                            Icon(
                                imageVector = if (test.testType == "WATER_TEST") Icons.Default.WaterDrop else Icons.Default.Biotech,
                                contentDescription = null,
                                tint = PrimaryDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = test.sampleName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )
                            )
                            Text(
                                text = "${test.date} • Score: ${test.qualityScore}%",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariantDark
                                )
                            )
                        }
                    }

                    StatusBadge(status = test.qualityStatus)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
