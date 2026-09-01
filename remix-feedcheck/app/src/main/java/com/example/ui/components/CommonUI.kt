package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.NavigationScreen
import com.example.ui.theme.*

@Composable
fun FeedCheckTopBar(
    title: String = "FeedCheck",
    currentLanguage: AppLanguage,
    onLanguageToggle: () -> Unit,
    showAdminToggle: Boolean = true,
    onAdminToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceContainerLowest,
        shadowElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.testTag("app_header_logo")
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = "FeedCheck Logo",
                    tint = PrimaryDarkGreen,
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showAdminToggle && onAdminToggle != null) {
                    IconButton(
                        onClick = onAdminToggle,
                        modifier = Modifier.size(40.dp).testTag("admin_dashboard_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Admin Dashboard",
                            tint = PrimaryDarkGreen
                        )
                    }
                }

                Button(
                    onClick = onLanguageToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = PrimaryDarkGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PrimaryDarkGreen),
                        width = 1.5.dp
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("language_toggle_button")
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "EN/TEL" else "తెలుగు/EN",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDarkGreen
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FeedCheckBottomNav(
    currentScreen: NavigationScreen,
    onNavigate: (NavigationScreen) -> Unit,
    isTelugu: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceContainerLowest,
        shadowElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Icons.Default.Home,
                label = if (isTelugu) "హోమ్" else "Home",
                isSelected = currentScreen == NavigationScreen.HOME,
                onClick = { onNavigate(NavigationScreen.HOME) },
                testTag = "nav_home"
            )

            NavItem(
                icon = Icons.Default.Biotech,
                label = if (isTelugu) "పరీక్ష" else "Check Feed",
                isSelected = currentScreen == NavigationScreen.CHECK_FEED,
                onClick = { onNavigate(NavigationScreen.CHECK_FEED) },
                testTag = "nav_check_feed"
            )

            NavItem(
                icon = Icons.Default.History,
                label = if (isTelugu) "చరిత్ర" else "History",
                isSelected = currentScreen == NavigationScreen.HISTORY,
                onClick = { onNavigate(NavigationScreen.HISTORY) },
                testTag = "nav_history"
            )

            NavItem(
                icon = Icons.Default.Person,
                label = if (isTelugu) "ప్రొఫైల్" else "Profile",
                isSelected = currentScreen == NavigationScreen.PROFILE,
                onClick = { onNavigate(NavigationScreen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) SecondaryContainerGreen else Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) OnSecondaryContainerGreen else OnSurfaceVariantDark,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) PrimaryDarkGreen else OnSurfaceVariantDark,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status.lowercase()) {
        "optimal", "good", "safe" -> Triple(OptimalGreen, OnOptimalGreen, Icons.Default.CheckCircle)
        "deficient", "high", "high-risk" -> Triple(ErrorContainerRed, OnErrorContainerRed, Icons.Default.Warning)
        "warning", "moderate" -> Triple(WarningContainerOrange, OnWarningContainerOrange, Icons.Default.Info)
        else -> Triple(SurfaceContainerMid, OnSurfaceDark, Icons.Default.Pending)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
