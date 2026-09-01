package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FeedCheckColorScheme = lightColorScheme(
    primary = PrimaryDarkGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryContainerGreen,
    onPrimaryContainer = OnPrimaryContainerGreen,
    secondary = SecondaryGreen,
    onSecondary = OnPrimaryGreen,
    secondaryContainer = SecondaryContainerGreen,
    onSecondaryContainer = OnSecondaryContainerGreen,
    background = BrandSand,
    onBackground = OnSurfaceDark,
    surface = SurfaceContainerLowest,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerMid,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineColor,
    outlineVariant = BrandCardBorder,
    error = ErrorRed,
    onError = OnPrimaryGreen,
    errorContainer = ErrorContainerRed,
    onErrorContainer = OnErrorContainerRed
)

@Composable
fun FeedCheckTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FeedCheckColorScheme,
        typography = Typography,
        content = content
    )
}
