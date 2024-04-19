package ch.admin.foitt.pilotwallet.theme

import androidx.compose.ui.graphics.Color

internal object Colors {
    val blue01 = Color(0xFF2F4356)
    val blue02 = Color(0xFF455F78)
    val blue03 = Color(0xFFCCE4F3)
    val blue04 = Color(0xFFD3E2EF)

    val green01 = Color(0xFF124A2F) // button confirm
    val green02 = Color(0xFF354F42)
    val green03 = Color(0xFF228354) // success background, green gradient top
    val green04 = Color(0xFF144C31) // green gradient bottom
    val green05 = Color(0xFF416E59) // button confirm active

    val white01 = Color(0xFFFFFFFF)
    val white02 = Color(0xFFF8FAFC)
    val white03 = Color(0x29FFFFFF)

    val black01 = Color(0xFF2C2C2E) // Normal texts and icons on bright background
    val black02 = Color(0xFF000000) // Shadows

    val grey01 = Color(0xFFF4EFEF) // Hover elements
    val grey02 = Color(0xFFE5E5EA) // Lines and disabled elements
    val grey03 = Color(0xFFC4C4C4) // Divider
    val grey05 = Color(0xFF8E8E93) // Label
    val grey06 = Color(0xFFFEFEFE) // Main background
    val grey07 = Color(0xFF1C1C1E) // Home title color
    val grey08 = Color(0xFFF4F4F4) // Background color of some icons
    val grey09 = Color(0xFFECEAED) // Police Qr Code background color
    val grey10 = Color(0xFF8A97A0) // Onboarding progress indicator inactive

    val red01 = Color(0xFF9F0000) // Main error text
    val red02 = Color(0xFFF6DCC7) // Error snackbar
    val red03 = Color(0xFFE94366) // Red gradient top
    val red04 = Color(0xFF5F0000) // Red gradient bottom
    val red05 = Color(0xFFAA0A0F) // Pin input error gradient left
    val red06 = Color(0xFFE23D5D) // Pin input error gradient right
}

// The light scrim color used in the platform API 29+
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/com/android/internal/policy/DecorView.java;drc=6ef0f022c333385dba2c294e35b8de544455bf19;l=142
val DefaultLightScrim = Color(0xFFe6FFFF)

// The dark scrim color used in the platform.
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/res/color/system_bar_background_semi_transparent.xml
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/remote_color_resources_res/values/colors.xml;l=67
val DefaultDarkScrim = Color(0x801b1b1b)
