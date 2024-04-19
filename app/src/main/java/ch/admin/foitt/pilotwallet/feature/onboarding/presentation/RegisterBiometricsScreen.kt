package ch.admin.foitt.pilotwallet.feature.onboarding.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.admin.foitt.pilotwallet.R
import ch.admin.foitt.pilotwallet.feature.onboarding.navigation.OnboardingNavGraph
import ch.admin.foitt.pilotwallet.platform.biometrics.presentation.OnboardingBiometricsContent
import ch.admin.foitt.pilotwallet.platform.composables.ButtonOutlined
import ch.admin.foitt.pilotwallet.platform.composables.ButtonPrimary
import ch.admin.foitt.pilotwallet.platform.composables.LoadingOverlay
import ch.admin.foitt.pilotwallet.platform.composables.OnboardingTopBarButton
import ch.admin.foitt.pilotwallet.platform.composables.ScrollableWithStickyBottom
import ch.admin.foitt.pilotwallet.platform.navArgs.domain.model.RegisterBiometricsNavArg
import ch.admin.foitt.pilotwallet.platform.preview.WalletAllScreenPreview
import ch.admin.foitt.pilotwallet.platform.utils.LocalActivity
import ch.admin.foitt.pilotwallet.platform.utils.OnResumeEventHandler
import ch.admin.foitt.pilotwallet.theme.PilotWalletTheme
import ch.admin.foitt.pilotwallet.theme.Sizes
import com.ramcosta.composedestinations.annotation.Destination

@OnboardingNavGraph
@Destination(
    navArgsDelegate = RegisterBiometricsNavArg::class,
)
@Composable
fun RegisterBiometricsScreen(viewModel: RegisterBiometricsViewModel) {
    OnResumeEventHandler {
        viewModel.refreshScreenState()
    }

    val screenState = viewModel.screenState.collectAsStateWithLifecycle().value
    val currentActivity = LocalActivity.current

    RegisterBiometricsContent(
        onTriggerPrompt = { viewModel.enableBiometrics(currentActivity) },
        onOpenSettings = viewModel::openSettings,
        onSkip = viewModel::declineBiometrics,
        screenState = screenState,
    )

    LoadingOverlay(
        showOverlay = viewModel.initializationInProgress.collectAsStateWithLifecycle().value
    )
}

@Composable
private fun RegisterBiometricsContent(
    onTriggerPrompt: () -> Unit,
    onOpenSettings: () -> Unit,
    onSkip: () -> Unit,
    screenState: RegisterBiometricsScreenState
) = when (screenState) {
    RegisterBiometricsScreenState.Initial -> {}
    RegisterBiometricsScreenState.Available -> BiometricsAvailableContent(
        onTriggerPrompt = onTriggerPrompt,
        onSkip = onSkip,
    )
    RegisterBiometricsScreenState.Lockout,
    RegisterBiometricsScreenState.Error -> BiometricsLockedContent(
        onSkip = onSkip,
    )
    RegisterBiometricsScreenState.Disabled -> BiometricsDisabledContent(
        onOpenSettings = onOpenSettings,
        onSkip = onSkip,
    )
}

@Composable
private fun BiometricsAvailableContent(
    onTriggerPrompt: () -> Unit,
    onSkip: () -> Unit,
) = Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.TopEnd,
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
            OnboardingBiometricsContent(
                header = R.string.biometricSetup_title,
                description = R.string.biometricSetup_content,
                infoText = R.string.biometricSetup_reason,
            )
        },
        stickyBottomContent = {
            ButtonPrimary(
                text = stringResource(
                    id = R.string.biometricSetup_actionButton,
                    stringResource(id = R.string.biometricSetup_biometrics_text)
                ),
                leftIcon = painterResource(id = R.drawable.pilot_ic_checkmark_button),
                onClick = onTriggerPrompt,
            )
        }
    )
}

@Composable
private fun BiometricsDisabledContent(
    onOpenSettings: () -> Unit,
    onSkip: () -> Unit,
) = ScrollableWithStickyBottom(
    scrollableContent = {
        OnboardingBiometricsContent(
            header = R.string.biometricSetup_title,
            description = R.string.biometricSetup_disabled_content,
            infoText = R.string.biometricSetup_reason,
        )
    },
    stickyBottomContent = {
        ButtonOutlined(
            text = stringResource(id = R.string.biometricSetup_disabled_skipButton),
            leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
            onClick = onSkip
        )
        Spacer(modifier = Modifier.height(Sizes.s04))
        ButtonPrimary(
            text = stringResource(id = R.string.biometricSetup_disabled_enableButton),
            rightIcon = painterResource(id = R.drawable.pilot_ic_settings_link),
            onClick = onOpenSettings
        )
    }
)

@Composable
private fun BiometricsLockedContent(
    onSkip: () -> Unit,
) = ScrollableWithStickyBottom(
    scrollableContent = {
        OnboardingBiometricsContent(
            header = R.string.biometricSetup_title,
            description = R.string.biometricSetup_lockout_text,
            infoText = R.string.biometricSetup_reason,
        )
    },
    stickyBottomContent = {
        ButtonOutlined(
            text = stringResource(id = R.string.biometricSetup_disabled_skipButton),
            leftIcon = painterResource(id = R.drawable.pilot_ic_cross),
            onClick = onSkip
        )
    }
)

private class RegisterBiometricsPreviewParams : PreviewParameterProvider<RegisterBiometricsScreenState> {
    override val values = sequenceOf(
        RegisterBiometricsScreenState.Available,
        RegisterBiometricsScreenState.Disabled,
        RegisterBiometricsScreenState.Lockout,
        RegisterBiometricsScreenState.Error,
        RegisterBiometricsScreenState.Initial,
    )
}

@WalletAllScreenPreview
@Composable
private fun RegisterBiometricsPreview(
    @PreviewParameter(RegisterBiometricsPreviewParams::class) previewParams: RegisterBiometricsScreenState
) {
    PilotWalletTheme {
        RegisterBiometricsContent(
            onTriggerPrompt = {},
            onSkip = {},
            onOpenSettings = {},
            screenState = previewParams,
        )
    }
}
