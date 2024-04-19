package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.feature.onboarding.presentation.composables.OnboardingScreenContent
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.OnboardingTopBarButton
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@OnboardingNavGraph(start = true)
@Destination
@Composable
fun OnboardingIntroScreen(
    viewModel: OnboardingIntroViewModel,
) {
    OnboardingIntroScreenContent(
        onSkip = viewModel::onSkip,
        onNext = viewModel::onNext,
    )
}

@Composable
private fun OnboardingIntroScreenContent(
    onSkip: () -> Unit,
    onNext: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        OnboardingTopBarButton(
            modifier = Modifier
                .padding(end = Sizes.s02)
                .statusBarsPadding()
                .zIndex(1f),
            text = stringResource(id = R.string.biometricSetup_dismissButton),
            onClick = onSkip,
        )

        ScrollableWithStickyBottom(
            scrollableContent = {
                OnboardingScreenContent(
                    title = stringResource(id = R.string.onboarding_wallet_primary),
                    image = painterResource(id = R.drawable.pilot_ic_cards),
                    subtitle = stringResource(id = R.string.onboarding_wallet_secondary),
                    currentStep = 1
                )
            },
            stickyBottomContent = {
                ButtonPrimary(
                    text = stringResource(id = R.string.onboarding_continue),
                    rightIcon = painterResource(id = R.drawable.pilot_ic_next_button),
                    onClick = onNext,
                )
            }
        )
    }
}

@WalletAllScreenPreview
@Composable
private fun OnboardingIntroScreenPreview() {
    PilotWalletTheme {
        OnboardingIntroScreenContent(
            onSkip = {},
            onNext = {},
        )
    }
}
