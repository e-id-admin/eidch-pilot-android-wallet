package ch.admin.foitt.pilotwallet.platform.onboarding.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.onboardingProgressStepInactive

@Composable
fun OnboardingProgressIndicator(
    currentStep: Int,
    maxSteps: Int = 4,
) = Row(
    modifier = Modifier.fillMaxWidth().padding(Sizes.s04),
    horizontalArrangement = Arrangement.Center
) {
    repeat(maxSteps) { index ->
        OnboardingProgressStep(isActive = index == currentStep - 1)
        if (index < maxSteps - 1) {
            Spacer(modifier = Modifier.width(Sizes.s02))
        }
    }
}

@Composable
private fun OnboardingProgressStep(
    isActive: Boolean,
) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onboardingProgressStepInactive
    val width = if (isActive) Sizes.s06 else Sizes.onboardingDotSize
    val height = Sizes.onboardingDotSize

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .requiredSize(width = width, height = height)
            .border(
                width = 2.dp,
                color = color,
                shape = CircleShape
            )
            .background(color)
    )
}

@Preview(showBackground = true)
@Composable
private fun OnboardingProgressIndicatorPreview() {
    PilotWalletTheme {
        OnboardingProgressIndicator(currentStep = 2)
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingProgressStepPreview() {
    PilotWalletTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Sizes.s04)
        ) {
            OnboardingProgressStep(isActive = true)
            OnboardingProgressStep(isActive = false)
        }
    }
}
