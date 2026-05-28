package bi.lan.lan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = BrandGreenLight,
    onPrimaryContainer = BrandGreenDark,
    secondary = BrandBlue,
    onSecondary = SurfaceWhite,
    secondaryContainer = BrandBlueLight,
    onSecondaryContainer = BrandBlueDark,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    error = StatusFailed,
    onError = SurfaceWhite,
    outline = InputBorder,
    outlineVariant = DividerColor
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    onPrimary = DarkBackground,
    primaryContainer = BrandGreenDark,
    onPrimaryContainer = BrandGreenLight,
    secondary = BrandBlue,
    onSecondary = DarkBackground,
    secondaryContainer = BrandBlueDark,
    onSecondaryContainer = BrandBlueLight,
    background = DarkBackground,
    onBackground = SurfaceWhite,
    surface = DarkSurface,
    onSurface = SurfaceWhite,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextHint,
    error = StatusFailed,
    onError = SurfaceWhite,
    outline = DarkSurfaceCard,
    outlineVariant = DarkSurface
)

@Composable
fun LANTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}