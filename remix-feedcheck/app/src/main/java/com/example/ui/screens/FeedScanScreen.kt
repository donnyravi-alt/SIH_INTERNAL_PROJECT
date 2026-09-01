package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.FeedTestEntity
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.FeedScanStep
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun FeedScanScreen(
    viewModel: FeedCheckViewModel,
    language: AppLanguage,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTelugu = language == AppLanguage.TELUGU
    val scanStep by viewModel.scanStep.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val selectedSmell by viewModel.selectedSmell.collectAsState()
    val currentAssessment by viewModel.currentAssessment.collectAsState()
    val recentTests by viewModel.testHistory.collectAsState()

    val detectingProgress by viewModel.detectingFeedProgress.collectAsState()
    val searchingParticlesProgress by viewModel.searchingParticlesProgress.collectAsState()
    val mouldProgress by viewModel.mouldIndicatorsProgress.collectAsState()
    val textureProgress by viewModel.textureProgress.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.startFeedAnalysis(null)
        }
    }

    when (scanStep) {
        FeedScanStep.CAPTURE -> {
            CaptureScreen(
                isTelugu = isTelugu,
                onCapture = { viewModel.startFeedAnalysis(null) },
                onOpenGallery = { photoPickerLauncher.launch("image/*") },
                onBack = onBackToHome,
                modifier = modifier
            )
        }
        FeedScanStep.ANALYZING -> {
            AnalyzingScreen(
                isTelugu = isTelugu,
                detectingProgress = detectingProgress,
                searchingParticlesProgress = searchingParticlesProgress,
                mouldProgress = mouldProgress,
                textureProgress = textureProgress,
                onCancel = { viewModel.setScanStep(FeedScanStep.CAPTURE) },
                modifier = modifier
            )
        }
        FeedScanStep.QUESTIONS -> {
            QuestionsScreen(
                isTelugu = isTelugu,
                selectedSmell = selectedSmell,
                onSelectSmell = { viewModel.setSelectedSmell(it) },
                onBack = { viewModel.setScanStep(FeedScanStep.CAPTURE) },
                onNext = { viewModel.submitQuestionnaireAndCalculateAssessment() },
                modifier = modifier
            )
        }
        FeedScanStep.ASSESSMENT -> {
            AssessmentScreen(
                isTelugu = isTelugu,
                qualityStatus = currentAssessment?.qualityStatus ?: "GOOD",
                qualityScore = currentAssessment?.qualityScore ?: 88,
                foreignParticles = currentAssessment?.foreignParticles ?: "NONE",
                mouldRisk = currentAssessment?.mouldRisk ?: "LOW",
                storageRisk = currentAssessment?.storageRisk ?: "MODERATE",
                recommendationEn = currentAssessment?.recommendationEn ?: "Safe for feeding today. Check moisture levels.",
                recommendationTe = currentAssessment?.recommendationTe ?: "ఈ రోజు ఆహారం ఇవ్వడానికి సురక్షితం. తేమ స్థాయిలను తనిఖీ చేయండి.",
                onViewAdvisory = { viewModel.viewAdvisoryForCurrent() },
                modifier = modifier
            )
        }
        FeedScanStep.ADVISORY -> {
            AdvisoryScreen(
                isTelugu = isTelugu,
                advisoryTextEn = currentAssessment?.advisoryEn ?: "Current feed mix lacks sufficient protein for high-yield dairy cows. We recommend increasing the alfalfa ratio by 15% to maintain milk production levels.",
                advisoryTextTe = currentAssessment?.advisoryTe ?: "ప్రస్తుత ఫీడ్ మిశ్రమంలో అధిక దిగుబడినిచ్చే ఆవులకు సరిపడా ప్రోటీన్ లేదు. ఆల్ఫాల్ఫా నిష్పత్తిని 15% పెంచాలని సిఫార్సు చేస్తున్నాము.",
                recentTests = recentTests,
                onSelectTest = { viewModel.selectHistoryItem(it) },
                onListenTts = { viewModel.speakCurrentAdvisory() },
                onBackToTests = { viewModel.setScanStep(FeedScanStep.CAPTURE) },
                modifier = modifier
            )
        }
    }
}

// -------------------------------------------------------------
// 1. CAPTURE SCREEN
// -------------------------------------------------------------
@Composable
private fun CaptureScreen(
    isTelugu: Boolean,
    onCapture: () -> Unit,
    onOpenGallery: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Viewport simulated background image
        AsyncImage(
            model = "https://lh3.googleusercontent.com/aida-public/AB6AXuCP9ZsDHsu8prNAPBS24Kjzt6mFH8-CF9BwuEBmwD355dzwiIe_kUw3ZnuU5-Oi4H5dQeIf1diTFzC--hY_ohYaxfe6UI9rsyC2-g8h9v5b3N5NhvfIrzsaz_QCA9brhScjp7md-ew_P5lHrFdyNW-OfAXoA0mQHUFV58BhjvW7B2r_iOmFs5IkZjyjTcBX9g1U2CrRBaBQxks0ApxwaZxDXXU0bmh-mYByTUzsL3Kvqt0iVN92_4EL",
            contentDescription = "Simulated Camera Viewfinder",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // Top back action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Camera Reticle Overlay & Guidance
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-60).dp)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isTelugu) "పెట్టెలో మేతను ఉంచండి" else "Place feed inside the box",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = if (isTelugu) "Place feed inside the box" else "పెట్టెలో మేతను ఉంచండి",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            // The Reticle Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .border(2.dp, PrimaryFixedGreen, RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PrimaryFixedGreen.copy(alpha = 0.5f),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Bottom Sheet Controls
        Surface(
            color = SurfaceLight,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Instructions List
                InstructionRow(
                    icon = Icons.Default.LightMode,
                    titleEn = "1. Ensure bright light",
                    titleTe = "ప్రకాశవంతమైన కాంతిని నిర్ధారించుకోండి"
                )
                InstructionRow(
                    icon = Icons.Default.WbTwilight,
                    titleEn = "2. Avoid shadows",
                    titleTe = "నీడలను నివారించండి"
                )
                InstructionRow(
                    icon = Icons.Default.Smartphone,
                    titleEn = "3. Hold phone 1 foot away",
                    titleTe = "ఫోన్‌ను 1 అడుగు దూరంలో ఉంచండి"
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action Controls (Gallery, Shutter Button, Flash)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onOpenGallery() }
                            .padding(8.dp)
                            .testTag("gallery_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = OnSurfaceVariantDark,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (isTelugu) "గ్యాలరీ" else "Gallery",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariantDark)
                        )
                    }

                    // Main Capture Shutter
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(PrimaryDarkGreen)
                            .border(4.dp, SurfaceLight, CircleShape)
                            .border(6.dp, PrimaryDarkGreen, CircleShape)
                            .clickable { onCapture() }
                            .testTag("camera_shutter_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(2.dp, PrimaryFixedGreen, CircleShape)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { isFlashOn = !isFlashOn }
                            .padding(8.dp)
                            .testTag("flash_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Flash",
                            tint = if (isFlashOn) PrimaryGreen else OnSurfaceVariantDark,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = if (isTelugu) "ఫ్లాష్" else "Flash",
                            style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceVariantDark)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, titleEn: String, titleTe: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = titleEn,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
            Text(
                text = titleTe,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceVariantDark,
                    fontSize = 11.sp
                )
            )
        }
    }
}

// -------------------------------------------------------------
// 2. ANALYZING SCREEN
// -------------------------------------------------------------
@Composable
private fun AnalyzingScreen(
    isTelugu: Boolean,
    detectingProgress: Int,
    searchingParticlesProgress: Int,
    mouldProgress: Int,
    textureProgress: Int,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotate"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(
                text = if (isTelugu) "దాణా నమూనా విశ్లేషణ" else "Analyzing Feed Sample",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                )
            )
            Text(
                text = if (isTelugu) "AI మీ చిత్రాన్ని ప్రాసెస్ చేసే వరకు వేచి ఉండండి." else "Please wait while the AI processes your image.",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariantDark)
            )
        }

        // Image Scanning Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, SecondaryContainerGreen, RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAiO7tTh4_MhKEZm54wCfMjiXn0ypKLPYYvbgfFRVX0tZM_XnMKVJawNW7epWqVeFnrDpxPbLGouCsjwICH-iqBCxZ9JcoymDk7mojS1fAnEXbkOQMB4yYi4UjGYMzenmlaOoxrmPNaHggCRGwxmw9ePaZHwuIwcpmfDMTZ8c6QnDft96WF6_BMkhYGvCQSZgIENk9U-NBZwnPeGZLxJaC8xFrCtRy24k0XDbuUJVUX7W3E7RZNw1kC",
                contentDescription = "Scanning feed",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scanning badge
            Surface(
                color = SurfaceLight.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .border(1.dp, BrandCardBorder, RoundedCornerShape(8.dp))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = null,
                        tint = PrimaryDarkGreen,
                        modifier = Modifier
                            .size(18.dp)
                            .rotate(rotation)
                    )
                    Text(
                        text = if (isTelugu) "స్కాన్ చేస్తోంది..." else "Scanning...",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                }
            }
        }

        // Progress Checklist Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProgressCheckItem(
                    title = if (isTelugu) "పశుగ్రాసం గుర్తించడం (Detecting Feed)" else "Detecting Cattle Feed",
                    progress = detectingProgress
                )
                ProgressCheckItem(
                    title = if (isTelugu) "విదేశీ కణాల శోధన (Searching Foreign Particles)" else "Searching for Foreign Particles",
                    progress = searchingParticlesProgress
                )
                ProgressCheckItem(
                    title = if (isTelugu) "బూజు సూచికల తనిఖీ (Mould Indicators)" else "Checking for Mould Indicators",
                    progress = mouldProgress
                )
                ProgressCheckItem(
                    title = if (isTelugu) "ఆకృతి విశ్లేషణ (Analyzing Texture)" else "Analyzing Texture",
                    progress = textureProgress
                )
            }
        }

        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = PrimaryDarkGreen
            ),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(PrimaryDarkGreen),
                width = 2.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("cancel_analysis_button")
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = PrimaryDarkGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isTelugu) "విశ్లేషణ రద్దు చేయండి (Cancel Analysis)" else "Cancel Analysis",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
            )
        }
    }
}

@Composable
private fun ProgressCheckItem(
    title: String,
    progress: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (progress >= 100) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SecondaryGreen,
                modifier = Modifier.size(24.dp)
            )
        } else if (progress > 0) {
            CircularProgressIndicator(
                progress = { progress / 100f },
                strokeWidth = 2.5.dp,
                color = PrimaryDarkGreen,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = null,
                tint = OutlineVariantColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (progress > 0) OnSurfaceDark else OnSurfaceVariantDark
                    )
                )
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 100) SecondaryGreen else PrimaryDarkGreen
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                color = if (progress >= 100) SecondaryContainerGreen else PrimaryDarkGreen,
                trackColor = SurfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

// -------------------------------------------------------------
// 3. SMART QUESTIONS SCREEN
// -------------------------------------------------------------
@Composable
private fun QuestionsScreen(
    isTelugu: Boolean,
    selectedSmell: String,
    onSelectSmell: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step progress indicator (2 of 4)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(PrimaryDarkGreen))
            Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(PrimaryDarkGreen))
            Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHigh))
            Box(modifier = Modifier.weight(1f).height(8.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainerHigh))
        }

        // Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
        ) {
            Text(
                text = if (isTelugu) "సరిగా అర్థం చేసుకోవడానికి సహాయపడండి" else "Help us understand better",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = if (isTelugu) "Help us understand better" else "సరిగా అర్థం చేసుకోవడానికి మాకు సహాయపడండి",
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariantDark),
                textAlign = TextAlign.Center
            )
        }

        // Question Card
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
                Column {
                    Text(
                        text = if (isTelugu) "దాణా వాసన ఎలా ఉంది?" else "How does the feed smell?",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )
                    Text(
                        text = if (isTelugu) "How does the feed smell?" else "దాణా వాసన ఎలా ఉంది?",
                        style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariantDark)
                    )
                }

                // Option 1: Normal / Fresh
                OptionSelectCard(
                    titleEn = "Normal / Fresh",
                    titleTe = "సాధారణం / తాజా",
                    icon = Icons.Default.Grass,
                    iconBg = SecondaryContainerGreen,
                    iconTint = OnSecondaryContainerGreen,
                    isSelected = selectedSmell == "Normal / Fresh",
                    onClick = { onSelectSmell("Normal / Fresh") },
                    testTag = "smell_option_normal"
                )

                // Option 2: Sour / Bad Smell
                OptionSelectCard(
                    titleEn = "Sour / Bad Smell",
                    titleTe = "పుల్లటి / చెడు వాసన",
                    icon = Icons.Default.SentimentDissatisfied,
                    iconBg = ErrorContainerRed,
                    iconTint = OnErrorContainerRed,
                    isSelected = selectedSmell == "Sour / Bad Smell",
                    onClick = { onSelectSmell("Sour / Bad Smell") },
                    testTag = "smell_option_sour"
                )

                // Option 3: Musty / Damp
                OptionSelectCard(
                    titleEn = "Musty / Damp",
                    titleTe = "బూజు పట్టిన / తడిగా",
                    icon = Icons.Default.WaterDrop,
                    iconBg = SurfaceContainerHigh,
                    iconTint = OnSurfaceVariantDark,
                    isSelected = selectedSmell == "Musty / Damp",
                    onClick = { onSelectSmell("Musty / Damp") },
                    testTag = "smell_option_musty"
                )
            }
        }

        // Back & Next Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryDarkGreen
                ),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PrimaryDarkGreen),
                    width = 2.dp
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("question_back_button")
            ) {
                Text(
                    text = if (isTelugu) "Back (వెనుకకు)" else "Back (వెనుకకు)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                )
            }

            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryDarkGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("question_next_button")
            ) {
                Text(
                    text = if (isTelugu) "Next (తదుపరి)" else "Next (తదుపరి)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun OptionSelectCard(
    titleEn: String,
    titleTe: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = if (isSelected) SurfaceContainerMid else Color.White,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryDarkGreen else BrandCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBg)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = titleEn,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceDark
                        )
                    )
                    Text(
                        text = titleTe,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariantDark
                        )
                    )
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = PrimaryDarkGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. FINAL ASSESSMENT SCREEN
// -------------------------------------------------------------
@Composable
private fun AssessmentScreen(
    isTelugu: Boolean,
    qualityStatus: String,
    qualityScore: Int,
    foreignParticles: String,
    mouldRisk: String,
    storageRisk: String,
    recommendationEn: String,
    recommendationTe: String,
    onViewAdvisory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Assessment Gauge Card
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BrandCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = if (isTelugu) "తుది అంచనా / Final Assessment" else "Final Assessment / తుది అంచనా",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceDark
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Circular Score Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(130.dp)
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = SurfaceContainerHigh,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = PrimaryDarkGreen,
                            startAngle = -90f,
                            sweepAngle = 360f * (qualityScore / 100f),
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryDarkGreen,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Overall Quality: $qualityStatus",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                )
                Text(
                    text = if (isTelugu) "మొత్తం నాణ్యత: బాగుంది" else "మొత్తం నాణ్యత: బాగుంది",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariantDark)
                )
            }
        }

        // Summary Breakdown Bento Grid (3 cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Foreign Particles
            BentoRiskCard(
                icon = Icons.Default.Grass,
                titleEn = "Foreign Particles",
                titleTe = "విదేశీ కణాలు",
                badgeText = foreignParticles,
                badgeBg = SecondaryContainerGreen,
                badgeTextTint = OnSecondaryContainerGreen,
                modifier = Modifier.weight(1f)
            )

            // Card 2: Mould Risk
            BentoRiskCard(
                icon = Icons.Default.Coronavirus,
                titleEn = "Mould Risk",
                titleTe = "అచ్చు ప్రమాదం",
                badgeText = mouldRisk,
                badgeBg = PrimaryFixedGreen,
                badgeTextTint = OnPrimaryFixedGreen,
                modifier = Modifier.weight(1f)
            )

            // Card 3: Storage Risk
            BentoRiskCard(
                icon = Icons.Default.Warehouse,
                titleEn = "Storage Risk",
                titleTe = "నిల్వ ప్రమాదం",
                badgeText = storageRisk,
                badgeBg = TertiaryFixedColor,
                badgeTextTint = OnTertiaryFixedColor,
                modifier = Modifier.weight(1f)
            )
        }

        // Recommendation Card
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
                Text(
                    text = "RECOMMENDATION / సిఫార్సు",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OutlineColor,
                        letterSpacing = 1.sp
                    )
                )

                Text(
                    text = recommendationEn,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = OnSurfaceDark
                    )
                )

                Text(
                    text = recommendationTe,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariantDark
                    )
                )

                Button(
                    onClick = onViewAdvisory,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDarkGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 8.dp)
                        .testTag("view_advisory_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "View Advisory / సలహాను వీక్షించండి" else "View Advisory / సలహాను వీక్షించండి",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

val TertiaryFixedColor = Color(0xFFE4E4CC)
val OnTertiaryFixedColor = Color(0xFF1B1D0E)

@Composable
private fun BentoRiskCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titleEn: String,
    titleTe: String,
    badgeText: String,
    badgeBg: Color,
    badgeTextTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.border(1.dp, BrandCardBorder, RoundedCornerShape(14.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryDarkGreen,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = titleEn,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OnSurfaceDark
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = titleTe,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OutlineColor,
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = badgeBg,
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = badgeText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = badgeTextTint
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 5. NUTRITION ADVISORY & HISTORY SCREEN
// -------------------------------------------------------------
@Composable
private fun AdvisoryScreen(
    isTelugu: Boolean,
    advisoryTextEn: String,
    advisoryTextTe: String,
    recentTests: List<FeedTestEntity>,
    onSelectTest: (FeedTestEntity) -> Unit,
    onListenTts: () -> Unit,
    onBackToTests: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandSand)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Advisory Section Card
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTelugu) "పోషణ సలహా (Nutrition Advisory)" else "Nutrition Advisory",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )

                    // Audio Playback / TTS button
                    IconButton(
                        onClick = onListenTts,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SecondaryContainerGreen)
                            .testTag("tts_listen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Listen to advisory",
                            tint = OnSecondaryContainerGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Text(
                    text = if (isTelugu) advisoryTextTe else advisoryTextEn,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = OnSurfaceDark,
                        lineHeight = 26.sp
                    )
                )

                Text(
                    text = if (isTelugu) "హెచ్చరిక: ట్రేస్ మినరల్స్ సిఫార్సు చేసిన పరిమితి కంటే తక్కువగా ఉన్నాయి. తక్షణ మినరల్ మిశ్రమం అవసరం." else "Alert: Trace minerals are below the recommended threshold. Immediate supplementation required.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = ErrorRed
                    )
                )

                Button(
                    onClick = { /* Request lab test */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryDarkGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("request_lab_test_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTelugu) "ల్యాబ్ పరీక్ష అభ్యర్థించండి (Request Lab Test)" else "Request Lab Test",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }

        // Recent Tests Section
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = if (isTelugu) "ఇటీవలి పరీక్షలు (Recent Tests)" else "Recent Tests",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            recentTests.forEach { test ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandCardBorder, RoundedCornerShape(14.dp))
                        .clickable { onSelectTest(test) }
                        .testTag("recent_test_item_${test.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = test.sampleName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurfaceDark
                                )
                            )
                            Text(
                                text = test.date,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariantDark
                                )
                            )
                        }

                        StatusBadge(status = test.qualityStatus)
                    }
                }
            }
        }
    }
}
