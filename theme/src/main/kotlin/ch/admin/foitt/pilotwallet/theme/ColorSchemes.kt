package ch.admin.foitt.pilotwallet.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

internal val debugColor = Color.Magenta

internal val LightColorScheme = lightColorScheme(
    primary = Colors.blue01,
    secondary = Colors.white02,
    tertiary = Colors.green01, // Tertiary will be the success color
    background = Colors.white01,
    onBackground = Colors.black01,
    surface = Colors.white01,
    onSurface = Colors.black01,
    surfaceVariant = Colors.grey02,
    onSurfaceVariant = Colors.blue01,
    primaryContainer = Colors.blue01,
    onPrimaryContainer = Colors.white02,
    secondaryContainer = Colors.white01,
    onSecondaryContainer = Colors.black01,
    tertiaryContainer = Colors.green03,
    onTertiaryContainer = Colors.white02,
    scrim = DefaultDarkScrim,
    onPrimary = Colors.white02,
    onSecondary = Colors.blue01,
    onTertiary = Colors.white02,
    error = Colors.red01,
    onError = Colors.white01,
    errorContainer = Colors.red02,
    onErrorContainer = Colors.red01,
    outline = Colors.blue01,
    outlineVariant = Colors.grey02,
    inversePrimary = debugColor,
    surfaceTint = Color.Transparent,
    inverseOnSurface = debugColor,
    inverseSurface = debugColor,
)

// TODO support dark mode
internal val DarkColorScheme = LightColorScheme

// TODO support custom color scheme
val ColorScheme.errorBackgroundLight @Stable get() = Colors.red03
val ColorScheme.errorBackgroundDark @Stable get() = Colors.red04
val ColorScheme.onErrorBackgroundDark @Stable get() = Colors.white01
val ColorScheme.warningLight @Stable get() = Colors.red04
val ColorScheme.onWarningBackgroundDark @Stable get() = Colors.white01
val ColorScheme.successBackgroundLight @Stable get() = Colors.green03
val ColorScheme.successBackgroundDark @Stable get() = Colors.green04
val ColorScheme.labelBackground @Stable get() = Colors.white03
val ColorScheme.activeButtonBackground @Stable get() = Colors.green05
val ColorScheme.policeQrCodeBackground @Stable get() = Colors.grey09
val ColorScheme.onboardingProgressStepInactive @Stable get() = Colors.grey10
val ColorScheme.textLabels @Stable get() = Colors.grey05
val ColorScheme.textOutline @Stable get() = Colors.grey02
