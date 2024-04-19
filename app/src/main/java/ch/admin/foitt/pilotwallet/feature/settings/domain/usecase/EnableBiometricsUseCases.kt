package ch.admin.foitt.pilotwallet.feature.settings.domain.usecase

import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.BiometricsStatus
import ch.admin.foitt.pilotwallet.platform.biometricPrompt.domain.usecase.GetAuthenticators
import ch.admin.foitt.pilotwallet.platform.biometrics.domain.usecase.EnableBiometrics
import javax.inject.Inject

data class EnableBiometricsUseCases @Inject constructor(
    val biometricsStatus: BiometricsStatus,
    val getAuthenticators: GetAuthenticators,
    val enableBiometrics: EnableBiometrics,
)
