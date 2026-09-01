package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppLanguage
import com.example.ui.FeedCheckViewModel
import com.example.ui.FeedScanStep
import com.example.ui.NavigationScreen
import com.example.ui.components.FeedCheckBottomNav
import com.example.ui.components.FeedCheckTopBar
import com.example.ui.screens.*
import com.example.ui.theme.BrandSand
import com.example.ui.theme.FeedCheckTheme
import com.example.ui.theme.PrimaryDarkGreen

class MainActivity : ComponentActivity() {
    private val viewModel: FeedCheckViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeedCheckTheme {
                FeedCheckApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FeedCheckApp(viewModel: FeedCheckViewModel) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val scanStep by viewModel.scanStep.collectAsStateWithLifecycle()

    val isTelugu = language == AppLanguage.TELUGU

    // Determine top bar visibility and title
    val topBarTitle = when (currentScreen) {
        NavigationScreen.ADMIN_DASHBOARD -> if (isTelugu) "సహకార నిర్వాహక డ్యాష్‌బోర్డ్" else "FeedCheck Overview"
        NavigationScreen.CHATBOT -> if (isTelugu) "AI పశు సలహాదారు" else "AI Cattle Advisor"
        NavigationScreen.WATER_TEST -> if (isTelugu) "నీటి సెట్లింగ్ పరీక్ష" else "Water Settling Test"
        NavigationScreen.CHECK_FEED -> {
            when (scanStep) {
                FeedScanStep.CAPTURE -> if (isTelugu) "దాణా స్కాన్" else "Check Cattle Feed"
                FeedScanStep.ANALYZING -> if (isTelugu) "దాణా విశ్లేషణ" else "Analyzing Feed Sample"
                FeedScanStep.QUESTIONS -> if (isTelugu) "ప్రశ్నావళి" else "Smart Questions"
                FeedScanStep.ASSESSMENT -> if (isTelugu) "తుది అంచనా" else "Final Assessment"
                FeedScanStep.ADVISORY -> if (isTelugu) "పోషణ సలహా" else "Nutrition Advisory"
            }
        }
        NavigationScreen.HISTORY -> if (isTelugu) "నా పరీక్షల చరిత్ర" else "Test History"
        NavigationScreen.PROFILE -> if (isTelugu) "రైతు ప్రొఫైల్" else "Farm Profile"
        else -> "FeedCheck"
    }

    val showBottomNav = currentScreen in listOf(
        NavigationScreen.HOME,
        NavigationScreen.HISTORY,
        NavigationScreen.PROFILE
    ) || (currentScreen == NavigationScreen.CHECK_FEED && scanStep == FeedScanStep.ADVISORY)

    val showTopBar = currentScreen != NavigationScreen.CHATBOT &&
            !(currentScreen == NavigationScreen.CHECK_FEED && scanStep == FeedScanStep.CAPTURE)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            // Tablet Navigation Rail
            if (isExpandedScreen) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    NavigationRailItem(
                        selected = currentScreen == NavigationScreen.HOME,
                        onClick = { viewModel.navigateTo(NavigationScreen.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(if (isTelugu) "హోమ్" else "Home") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == NavigationScreen.CHECK_FEED,
                        onClick = {
                            viewModel.setScanStep(FeedScanStep.CAPTURE)
                            viewModel.navigateTo(NavigationScreen.CHECK_FEED)
                        },
                        icon = { Icon(Icons.Default.Biotech, contentDescription = "Check") },
                        label = { Text(if (isTelugu) "పరీక్ష" else "Check") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == NavigationScreen.HISTORY,
                        onClick = { viewModel.navigateTo(NavigationScreen.HISTORY) },
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text(if (isTelugu) "చరిత్ర" else "History") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == NavigationScreen.PROFILE,
                        onClick = { viewModel.navigateTo(NavigationScreen.PROFILE) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(if (isTelugu) "ప్రొఫైల్" else "Profile") }
                    )
                    NavigationRailItem(
                        selected = currentScreen == NavigationScreen.ADMIN_DASHBOARD,
                        onClick = { viewModel.navigateTo(NavigationScreen.ADMIN_DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Admin") },
                        label = { Text(if (isTelugu) "అడ్మిన్" else "Admin") }
                    )
                }
            }

            // Main Screen Scaffold
            Scaffold(
                topBar = {
                    if (showTopBar) {
                        FeedCheckTopBar(
                            title = topBarTitle,
                            currentLanguage = language,
                            onLanguageToggle = { viewModel.toggleLanguage() },
                            showAdminToggle = currentScreen != NavigationScreen.ADMIN_DASHBOARD,
                            onAdminToggle = { viewModel.navigateTo(NavigationScreen.ADMIN_DASHBOARD) }
                        )
                    }
                },
                bottomBar = {
                    if (!isExpandedScreen && showBottomNav) {
                        FeedCheckBottomNav(
                            currentScreen = currentScreen,
                            onNavigate = { screen ->
                                if (screen == NavigationScreen.CHECK_FEED) {
                                    viewModel.setScanStep(FeedScanStep.CAPTURE)
                                }
                                viewModel.navigateTo(screen)
                            },
                            isTelugu = isTelugu
                        )
                    }
                },
                containerColor = BrandSand,
                contentWindowInsets = WindowInsets.statusBars
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        NavigationScreen.HOME -> {
                            HomeScreen(
                                language = language,
                                onNavigate = { viewModel.navigateTo(it) },
                                onStartScan = {
                                    viewModel.setScanStep(FeedScanStep.CAPTURE)
                                    viewModel.navigateTo(NavigationScreen.CHECK_FEED)
                                }
                            )
                        }
                        NavigationScreen.CHECK_FEED -> {
                            FeedScanScreen(
                                viewModel = viewModel,
                                language = language,
                                onBackToHome = { viewModel.navigateTo(NavigationScreen.HOME) }
                            )
                        }
                        NavigationScreen.WATER_TEST -> {
                            WaterTestScreen(
                                viewModel = viewModel,
                                language = language,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                        NavigationScreen.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                language = language,
                                onSelectTest = { test -> viewModel.selectHistoryItem(test) }
                            )
                        }
                        NavigationScreen.PROFILE -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                language = language,
                                onNavigate = { viewModel.navigateTo(it) }
                            )
                        }
                        NavigationScreen.ADMIN_DASHBOARD -> {
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                language = language,
                                onBack = { viewModel.navigateTo(NavigationScreen.HOME) }
                            )
                        }
                        NavigationScreen.CHATBOT -> {
                            CattleAdvisorChatScreen(
                                viewModel = viewModel,
                                language = language,
                                onBack = { viewModel.navigateTo(NavigationScreen.HOME) }
                            )
                        }
                    }
                }
            }
        }
    }
}
