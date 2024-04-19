package ch.admin.foitt.pilotwallet.feature.onboarding.domain.usecase

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.BiometricsStatus
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.GetAuthenticators
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.EnableBiometrics
import ch.admin.foitt.pilotwallet.platform.onboardingState.domain.SaveOnboardingState
import ch.admin.foitt.pilotwallet.platform.passphrase.domain.usecase.InitializePassphrase
import ch.admin.foitt.pilotwallet.platform.scaffold.domain.usecase.SetErrorDialogState
import javax.inject.Inject

data class RegisterBiometricsUseCases @Inject constructor(
    val enableBiometrics: EnableBiometrics,
    val initializePassphrase: InitializePassphrase,
    val biometricsStatus: BiometricsStatus,
    val getAuthenticators: GetAuthenticators,
    val saveOnboardingState: SaveOnboardingState,
    val setErrorDialogState: SetErrorDialogState,
)
