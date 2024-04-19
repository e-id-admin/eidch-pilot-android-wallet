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

@OnboardingNavGraph
@Destination
@Composable
fun OnboardingQRScreen(
    viewModel: OnboardingQRViewModel,
) {
    OnboardingQRScreenContent(
        onNext = viewModel::onNext,
    )
}

@Composable
private fun OnboardingQRScreenContent(
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
            onClick = onNext,
        )

        ScrollableWithStickyBottom(
            scrollableContent = {
                OnboardingScreenContent(
                    title = stringResource(id = R.string.onboarding_qrCode_primary),
                    image = painterResource(id = R.drawable.pilot_ic_qr_scan),
                    subtitle = stringResource(id = R.string.onboarding_qrCode_secondary),
                    currentStep = 2,
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
private fun OnboardingQRScreenPreview() {
    PilotWalletTheme {
        OnboardingQRScreenContent(
            onNext = {},
        )
    }
}
