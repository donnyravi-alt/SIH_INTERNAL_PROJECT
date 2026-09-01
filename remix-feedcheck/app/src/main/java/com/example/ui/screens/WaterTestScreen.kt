package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.NavigationScreen
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun WaterTestScreen(
    viewModel: FeedCheckViewModel,
    language: AppLanguage,
    onNavigate: (NavigationScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU
    val isRecording by viewModel.isWaterRecording.collectAsState()
    val progress by viewModel.waterTestProgress.collectAsState()
    val isFinished by viewModel.waterTestFinished.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Instructions Card
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
                Text(
                    text = if (isTelugu) "నీటి సెట్లింగ్ పరీక్ష విధానం" else "Water Settling Test Instructions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Water,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (isTelugu) "1. పారదర్శక గ్లాసు నీటిలో దాణా కలపండి" else "1. Add Feed sample to glass of water",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = OnSurfaceDark
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (isTelugu) "2. 30 సెకన్ల పాటు బాగా తిప్పండి (కలపండి)" else "2. Stir thoroughly for 30 seconds",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = OnSurfaceDark
                        )
                    )
                }
            }
        }

        // Viewfinder / Water Camera Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, PrimaryFixedGreen, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCP9ZsDHsu8prNAPBS24Kjzt6mFH8-CF9BwuEBmwD355dzwiIe_kUw3ZnuU5-Oi4H5dQeIf1diTFzC--hY_ohYaxfe6UI9rsyC2-g8h9v5b3N5NhvfIrzsaz_QCA9brhScjp7md-ew_P5lHrFdyNW-OfAXoA0mQHUFV58BhjvW7B2r_iOmFs5IkZjyjTcBX9g1U2CrRBaBQxks0ApxwaZxDXXU0bmh-mYByTUzsL3Kvqt0iVN92_4EL",
                contentDescription = "Water Test Glass",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlays & Progress
            if (isRecording) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress / 100f },
                            strokeWidth = 6.dp,
                            color = PrimaryFixedGreen,
                            modifier = Modifier.size(68.dp)
                        )
                        Text(
                            text = if (isTelugu) "సెట్లింగ్ రికార్డ్ చేస్తోంది... (${progress / 20}s)" else "Recording settling pattern... (${progress / 20}s)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Action Buttons or Results Card
        if (!isFinished) {
            Button(
                onClick = { viewModel.startWaterSettlingTest() },
                enabled = !isRecording,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDarkGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_water_test_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTelugu) "విశ్లేషణ ప్రారంభించండి (Start 5s Test)" else "Start Analysis (5s)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        } else {
            // Result Card
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTelugu) "పరీక్ష ఫలితం: ఉత్తమం" else "Settling Quality: OPTIMAL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryDarkGreen
                            )
                        )
                        StatusBadge(status = "Optimal")
                    }

                    Text(
                        text = if (isTelugu) "ఇసుక లేదా విదేశీ పదార్థాలు అడుగున చేరలేదు. స్వచ్ఛమైన కరిగే గుణం ఉంది." else "No dense silica or adulterant sedimentation detected. Light fiber suspended evenly.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceDark)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.resetWaterTest() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = OnSurfaceDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("Retest")
                        }

                        Button(
                            onClick = { onNavigate(NavigationScreen.HISTORY) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryDarkGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Text("View History")
                        }
                    }
                }
            }
        }
    }
}
