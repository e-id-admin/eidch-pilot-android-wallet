package ch.admin.foitt.pilotwallet.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Gradients {
    fun bottomFadingBrush(endColor: Color) = Brush.verticalGradient(
        0.0f to Color.Transparent,
        1.0f to endColor,
    )

    fun topFadingBrush(startColor: Color) = Brush.verticalGradient(
        0.0f to startColor,
        1.0f to Color.Transparent,
    )

    fun credentialBrush() = Brush.verticalGradient(
        0.0f to Colors.black02.copy(alpha = 0.25f),
        1.0f to Color.Transparent
    )

    fun credentialThumbnailBrush() = Brush.horizontalGradient(
        0.0f to Colors.black02.copy(alpha = 0.25f),
        1.0f to Color.Transparent
    )

    fun pinInputBrush() = Brush.horizontalGradient(
        colors = listOf(
            Colors.grey01,
            Colors.grey02,
        )
    )

    fun biometricsOnboardingBrush() = Brush.horizontalGradient(
        colors = listOf(
            Colors.grey01,
            Colors.grey02,
        )
    )

    fun pinInputErrorBrush() = Brush.horizontalGradient(
        colors = listOf(
            Colors.red05,
            Colors.red06,
        )
    )

    fun pinInputSuccessBrush() = Brush.horizontalGradient(
        colors = listOf(
            Colors.green03,
            Colors.green04,
        )
    )
}
