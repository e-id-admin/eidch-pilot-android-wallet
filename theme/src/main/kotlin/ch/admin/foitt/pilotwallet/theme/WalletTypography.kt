package ch.admin.foitt.pilotwallet.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal val typography = Typography(
    // Home title
    headlineLarge = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    // Credential Title
    headlineSmall = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    // Screen title
    titleLarge = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    // Top bar title
    titleMedium = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    titleSmall = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    bodyLarge = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    bodyMedium = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    // Credential Subtitle
    bodySmall = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    // Button main text
    labelLarge = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    labelMedium = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
    labelSmall = TextStyle(
        fontFamily = notoSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    ),
)
