package ch.admin.foitt.pilotwallet.feature.onboarding.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import ch.admin.foitt.pilotwallet.platform.onboarding.composables.OnboardingProgressIndicator
import ch.admin.foitt.pilotwallet.theme.Sizes
import ch.admin.foitt.pilotwallet.theme.WalletTexts

@Composable
fun OnboardingScreenContent(
    title: String,
    image: Painter,
    subtitle: String,
    currentStep: Int,
) {
    Image(
        modifier = Modifier.fillMaxWidth(),
        painter = image,
        contentDescription = null,
        alignment = Alignment.Center,
        contentScale = ContentScale.Fit,
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    OnboardingProgressIndicator(currentStep = currentStep)
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.TitleScreenCentered(
        modifier = Modifier.fillMaxWidth(),
        text = title,
    )
    Spacer(modifier = Modifier.height(Sizes.s04))
    WalletTexts.BodyCentered(
        text = subtitle,
        modifier = Modifier.fillMaxWidth(),
    )
}
